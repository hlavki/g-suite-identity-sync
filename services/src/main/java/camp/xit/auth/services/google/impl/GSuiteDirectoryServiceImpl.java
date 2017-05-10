package camp.xit.auth.services.google.impl;

import camp.xit.auth.services.google.GSuiteDirectoryService;
import camp.xit.auth.services.google.model.GroupMembership;
import camp.xit.auth.services.google.NoPrivateKeyException;
import camp.xit.auth.services.google.model.Group;
import camp.xit.auth.services.google.model.GroupList;
import camp.xit.auth.services.util.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.rs.security.jose.common.JoseType;
import org.apache.cxf.rs.security.jose.jwa.SignatureAlgorithm;
import org.apache.cxf.rs.security.jose.jws.JwsHeaders;
import org.apache.cxf.rs.security.jose.jws.JwsJwtCompactProducer;
import org.apache.cxf.rs.security.jose.jwt.JwtClaims;
import org.apache.cxf.rs.security.jose.jwt.JwtToken;
import org.apache.cxf.rs.security.oauth2.client.AccessTokenGrantWriter;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.grants.jwt.JwtBearerGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthJSONProvider;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSuiteDirectoryServiceImpl implements GSuiteDirectoryService, EventHandler {

    private static final Logger log = LoggerFactory.getLogger(GSuiteDirectoryServiceImpl.class);
    private static final String CLIENT_ID_PROP = "oauth2.serviceAccount.clientId";
    private static final String SUBJECT_PROP = "oauth2.serviceAccount.subject";
    private static final String PRIVATE_KEY_PROP = "oauth2.serviceAccount.privateKey.file";
    private static final String PRIVATE_KEY_PASS_PROP = "oauth2.serviceAccount.privateKey.passphrase";
    private static final String GSUITE_DOMAIN_PROP = "gsuite.domain";
    private static final int TOKEN_LIFETIME = 3600;
    private PrivateKey privateKey;
    private final WebClient directoryApiClient;
    private final Cache<Boolean, ClientAccessToken> tokenCache;
    private Configuration config;


    public GSuiteDirectoryServiceImpl(WebClient directoryApiClient) {
        this.directoryApiClient = directoryApiClient;
        this.tokenCache = Cache2kBuilder.of(Boolean.class, ClientAccessToken.class)
                .expireAfterWrite(TOKEN_LIFETIME - 3, TimeUnit.SECONDS)
                .loader((key) -> {
                    return key.equals(Boolean.TRUE) ? getAccessToken() : null;
                }).build();
    }


    private void configure() {
        log.info("Configuring GSuiteDirectoryService ...");
        try {
            privateKey = loadPrivateKey(config.get(PRIVATE_KEY_PROP), config.get(PRIVATE_KEY_PASS_PROP));
        } catch (NoPrivateKeyException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }


    @Override
    public GroupMembership getGroupMembers(String groupKey) {
        return readGroupMembers(groupKey, null);
    }


    private GroupMembership readGroupMembers(String groupKey, GroupMembership parent) {
        String path = MessageFormat.format("groups/{0}/members", new Object[]{groupKey});

        WebClient webClient = WebClient.fromClient(directoryApiClient, true).path(path);
        ClientAccessToken accessToken = tokenCache.get(true);
        webClient.authorization(accessToken);
        webClient.query("maxResults", 2);
        GroupMembership result;
        if (parent != null && parent.getNextPageToken() != null) {
            result = webClient.query("pageToken", parent.getNextPageToken()).get(GroupMembership.class);
            result.getMembers().addAll(parent.getMembers());
        } else {
            result = webClient.get(GroupMembership.class);
        }
        return result.getNextPageToken() != null ? readGroupMembers(groupKey, result) : result;
    }


    @Override
    public Group getGroup(String groupKey) {
        String path = MessageFormat.format("groups/{0}", new Object[]{groupKey});

        WebClient webClient = WebClient.fromClient(directoryApiClient, true);

        ClientAccessToken accessToken = tokenCache.get(true);
        webClient.authorization(accessToken);
        Group group = webClient.path(path).get(Group.class);
        return group;
    }


    @Override
    public GroupList getGroups(String userKey) {
        WebClient webClient = WebClient.fromClient(directoryApiClient, true).path("groups");

        webClient.authorization(tokenCache.get(true));
        if (userKey != null) {
            webClient.query("userKey", userKey);
        }
        GroupList groupList = webClient.query("domain", config.get(GSUITE_DOMAIN_PROP)).get(GroupList.class);
        return groupList;
    }


    private ClientAccessToken getAccessToken() {
        JwsHeaders headers = new JwsHeaders(JoseType.JWT, SignatureAlgorithm.RS256);
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(config.get(CLIENT_ID_PROP));
        claims.setAudience("https://accounts.google.com/o/oauth2/token");
        claims.setSubject(config.get(SUBJECT_PROP));

        long issuedAt = OAuthUtils.getIssuedAt();
        claims.setIssuedAt(issuedAt);
        claims.setExpiryTime(issuedAt + TOKEN_LIFETIME);
        claims.setProperty("scope", "https://www.googleapis.com/auth/admin.directory.group.readonly");

        JwtToken token = new JwtToken(headers, claims);
        JwsJwtCompactProducer p = new JwsJwtCompactProducer(token);
        String base64UrlAssertion = p.signWith(privateKey);

        JwtBearerGrant grant = new JwtBearerGrant(base64UrlAssertion);

        WebClient accessTokenService = WebClient.create("https://accounts.google.com/o/oauth2/token",
                Arrays.asList(new OAuthJSONProvider(), new AccessTokenGrantWriter()));
        WebClient.getConfig(accessTokenService).getInInterceptors().add(new LoggingInInterceptor());

        accessTokenService.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON);

        return accessTokenService.post(grant, ClientAccessToken.class);
    }


    private static PrivateKey loadPrivateKey(String keyFile, String password) throws NoPrivateKeyException {
        try (InputStream is = new FileInputStream(keyFile)) {
            KeyStore store = KeyStore.getInstance("PKCS12");
            store.load(is, password.toCharArray());
            return (PrivateKey) store.getKey("privateKey", password.toCharArray());
        } catch (IOException | CertificateException | KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new NoPrivateKeyException("Could not load private key", e);
        }
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            this.config = (Configuration) event.getProperty(Configuration.CONFIG_PROP);
            configure();
        }
    }
}

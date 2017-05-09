package camp.xit.auth.services.google.impl;

import camp.xit.auth.services.google.GSuiteDirectoryService;
import camp.xit.auth.services.google.GroupMembershipResponse;
import camp.xit.auth.services.google.NoPrivateKeyException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.text.MessageFormat;
import java.util.Arrays;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GSuiteDirectoryServiceImpl implements GSuiteDirectoryService {

    private static final Logger log = LoggerFactory.getLogger(GSuiteDirectoryServiceImpl.class);
    private PrivateKey privateKey;
    private final WebClient directoryApiClient;
    private final String clientId;
    private final String subject;


    public GSuiteDirectoryServiceImpl(WebClient directoryApiClient, String clientId, String subject, String keyResource, String keyPassword) {
        this.directoryApiClient = directoryApiClient;
        this.clientId = clientId;
        this.subject = subject;
        try {
            privateKey = loadPrivateKey(keyResource, keyPassword);
        } catch (NoPrivateKeyException e) {
            log.error(e.getMessage(), e.getCause());
        }
    }


    @Override
    public GroupMembershipResponse getGroupMembers(String groupKey) {
        String path = MessageFormat.format("groups/{0}/members", new Object[]{groupKey});

        WebClient webClient = WebClient.fromClient(directoryApiClient, true);

        ClientAccessToken accessToken = getAccessToken(privateKey, clientId, subject);
        webClient.authorization(accessToken);
        GroupMembershipResponse membership = webClient.path(path).get(GroupMembershipResponse.class);
        return membership;
    }


    private static ClientAccessToken getAccessToken(PrivateKey privateKey, String issuer, String subject) {
        JwsHeaders headers = new JwsHeaders(JoseType.JWT, SignatureAlgorithm.RS256);
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(issuer);
        claims.setAudience("https://accounts.google.com/o/oauth2/token");
        claims.setSubject(subject);

        long issuedAt = OAuthUtils.getIssuedAt();
        claims.setIssuedAt(issuedAt);
        claims.setExpiryTime(issuedAt + 60 * 60);
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
}

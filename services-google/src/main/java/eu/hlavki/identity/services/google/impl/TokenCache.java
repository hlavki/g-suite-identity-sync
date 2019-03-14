package eu.hlavki.identity.services.google.impl;

import com.google.common.base.Supplier;
import static com.google.common.base.Suppliers.memoizeWithExpiration;
import eu.hlavki.identity.services.google.NoPrivateKeyException;
import eu.hlavki.identity.services.google.config.Configurable;
import eu.hlavki.identity.services.google.config.Configuration;
import java.util.Arrays;
import static java.util.concurrent.TimeUnit.SECONDS;
import javax.ws.rs.core.MediaType;
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

public class TokenCache implements Configurable {

    private static final Logger log = LoggerFactory.getLogger(TokenCache.class);

    private final Configuration config;
    private Supplier<ClientAccessToken> tokenCache;


    public TokenCache(Configuration config) {
        this.config = config;
        reconfigure();
    }


    ClientAccessToken getToken() {
        return tokenCache.get();
    }


    @Override
    public void reconfigure() {
        log.info("Configuring token cache ...");
        long tokenLifetime = config.getServiceAccountTokenLifetime();
        log.info("Token lifetime set to {}", tokenLifetime);
        this.tokenCache = memoizeWithExpiration(() -> getAccessToken(), tokenLifetime - 3, SECONDS);
    }


    private ClientAccessToken getAccessToken() throws NoPrivateKeyException {
        JwsHeaders headers = new JwsHeaders(JoseType.JWT, SignatureAlgorithm.RS256);
        JwtClaims claims = new JwtClaims();
        claims.setIssuer(config.getServiceAccountClientId());
        claims.setAudience("https://accounts.google.com/o/oauth2/token");
        claims.setSubject(config.getServiceAccountSubject());

        long issuedAt = OAuthUtils.getIssuedAt();
        long tokenTimeout = config.getServiceAccountTokenLifetime();
        claims.setIssuedAt(issuedAt);
        claims.setExpiryTime(issuedAt + tokenTimeout);
        String scopes = String.join(" ", config.getServiceAccountScopes());
        claims.setProperty("scope", scopes);

        JwtToken token = new JwtToken(headers, claims);
        JwsJwtCompactProducer p = new JwsJwtCompactProducer(token);
        String base64UrlAssertion = p.signWith(config.readServiceAccountKey());

        JwtBearerGrant grant = new JwtBearerGrant(base64UrlAssertion);

        WebClient accessTokenService = WebClient.create("https://accounts.google.com/o/oauth2/token",
                Arrays.asList(new OAuthJSONProvider(), new AccessTokenGrantWriter()));

        accessTokenService.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON);

        return accessTokenService.post(grant, ClientAccessToken.class);
    }
}

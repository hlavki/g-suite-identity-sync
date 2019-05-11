package eu.hlavki.identity.services.rest.security;

import eu.hlavki.identity.services.google.NoPrivateKeyException;
import eu.hlavki.identity.services.google.config.Configuration;
import eu.hlavki.identity.services.rest.model.ServerError;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.oidc.common.IdToken;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.apache.cxf.rs.security.oidc.rp.OidcSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(Priorities.AUTHORIZATION)
public class SetupAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(SetupAuthorizationFilter.class);
    private final Configuration googleConfig;


    public SetupAuthorizationFilter(Configuration googleConfig) {
        this.googleConfig = googleConfig;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        OidcSecurityContext secCtx = (OidcSecurityContext) requestContext.getSecurityContext();
        OidcClientTokenContext tokenCtx = secCtx.getOidcContext();
        IdToken idToken = tokenCtx.getIdToken();
        String email = idToken.getEmail();
        boolean configured = false;
        try {
            configured = googleConfig.getServiceAccountEmail() != null && googleConfig.readServiceAccountKey() != null;
        } catch (NoPrivateKeyException e) {
        }
        if (configured) {
            log.error("Unauthorized access from {}. Application is already configured!", email);
            ServerError err = new ServerError("E002", "Unauthorized access to Configuration API");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(err).type(MediaType.APPLICATION_JSON).build());
        }
    }
}

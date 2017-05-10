package camp.xit.auth.services.rest;

import camp.xit.auth.services.google.GSuiteDirectoryService;
import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.oauth2.client.ClientTokenContext;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(Priorities.AUTHORIZATION)
public class GSuiteGroupAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GSuiteGroupAuthorizationFilter.class);
    private final GSuiteDirectoryService directoryService;
    private final String domain;


    public GSuiteGroupAuthorizationFilter(GSuiteDirectoryService directoryService, String domain) {
        this.directoryService = directoryService;
        this.domain = domain;
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        OidcClientTokenContext ctx = (OidcClientTokenContext) JAXRSUtils.getCurrentMessage().getContent(ClientTokenContext.class);
        String hdParam = ctx.getIdToken().getStringProperty("hd");
        if (!domain.equalsIgnoreCase(hdParam)) {
            log.error("Unauthorized access from {}", hdParam);
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}

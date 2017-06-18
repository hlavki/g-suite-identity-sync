package camp.xit.identity.services.rest.security;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.google.GSuiteDirectoryService;
import camp.xit.identity.services.google.ResourceNotFoundException;
import camp.xit.identity.services.google.model.GroupMembership;
import camp.xit.identity.services.model.ServerError;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.rs.security.oauth2.client.ClientTokenContext;
import org.apache.cxf.rs.security.oidc.common.IdToken;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(Priorities.AUTHORIZATION)
public class GSuiteGroupAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GSuiteGroupAuthorizationFilter.class);
    private final AppConfiguration config;
    private final Supplier<Set<String>> externalAccountsCache;


    public GSuiteGroupAuthorizationFilter(final GSuiteDirectoryService gsuiteDirService, AppConfiguration config) {
        this.config = config;
        this.externalAccountsCache = Suppliers.memoizeWithExpiration(
                () -> {
                    String allowGroup = config.getExternalAccountsGroup();
                    Set<String> result = Collections.emptySet();
                    try {
                        GroupMembership membership = gsuiteDirService.getGroupMembers(allowGroup);
                        result = membership.getMembers() == null ? Collections.emptySet()
                        : membership.getMembers().stream().map(m -> m.getEmail()).collect(Collectors.toSet());
                    } catch (ResourceNotFoundException e) {
                        log.warn("Group for external accounts {} does not exists", allowGroup);
                    }
                    return result;
                }, 15, TimeUnit.MINUTES);
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        OidcClientTokenContext ctx = (OidcClientTokenContext) JAXRSUtils.getCurrentMessage().getContent(ClientTokenContext.class);
        IdToken idToken = ctx.getIdToken();
        String email = idToken.getEmail();
        String hdParam = idToken.getStringProperty("hd");
        boolean fromGsuite = config.getGSuiteDomain().equalsIgnoreCase(hdParam);
        Set<String> externalAccounts = externalAccountsCache.get();
        if (!fromGsuite && !externalAccounts.contains(email)) {
            log.error("Unauthorized access from {}", hdParam);
            ServerError err = new ServerError("E001", "Sorry you are not allowed to exit camp");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(err).type(MediaType.APPLICATION_JSON).build());
        }
    }
}

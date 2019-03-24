package eu.hlavki.identity.services.rest.security;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.google.model.GroupMembership;
import eu.hlavki.identity.services.rest.model.ServerError;
import java.io.IOException;
import java.util.Collections;
import static java.util.Collections.emptySet;
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
    private final Supplier<Set<String>> externalAccountsCache;
    private final GSuiteDirectoryService gsuiteDirService;


    public GSuiteGroupAuthorizationFilter(final GSuiteDirectoryService gsuiteDirService, AppConfiguration config) {
        this.gsuiteDirService = gsuiteDirService;
        this.externalAccountsCache = Suppliers.memoizeWithExpiration(
                () -> config.getExternalAccountsGroup().map(g -> getExternalGroupMembers(g)).orElse(emptySet()),
                15, TimeUnit.MINUTES);
    }


    @Override

    public void filter(ContainerRequestContext requestContext) throws IOException {
        OidcClientTokenContext ctx = (OidcClientTokenContext) JAXRSUtils.getCurrentMessage().getContent(ClientTokenContext.class);
        IdToken idToken = ctx.getIdToken();
        String email = idToken.getEmail();
        String hdParam = idToken.getStringProperty("hd");
        boolean fromGsuite = gsuiteDirService.getDomainName().equalsIgnoreCase(hdParam);
        Set<String> externalAccounts = externalAccountsCache.get();
        if (!fromGsuite && !externalAccounts.contains(email)) {
            log.error("Unauthorized access from {}", hdParam);
            ServerError err = new ServerError("E001", "Sorry you are not allowed to enter this site");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(err).type(MediaType.APPLICATION_JSON).build());
        }
    }


    private Set<String> getExternalGroupMembers(String externalGroupName) {
        Set<String> result = emptySet();
        try {
            GroupMembership membership = gsuiteDirService.getGroupMembers(externalGroupName);
            result = membership.getMembers() == null ? Collections.emptySet()
                    : membership.getMembers().stream().map(m -> m.getEmail()).collect(Collectors.toSet());
        } catch (ResourceNotFoundException e) {
            log.warn("Group for external accounts {} does not exists", externalGroupName);
        }
        return result;
    }
}

package eu.hlavki.identity.services.rest.security;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.NoPrivateKeyException;
import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.google.model.GroupMembership;
import eu.hlavki.identity.services.rest.config.Configuration;
import eu.hlavki.identity.services.rest.model.ServerError;
import static eu.hlavki.identity.services.rest.model.ServerError.serverError;
import java.io.IOException;
import java.util.Collections;
import static java.util.Collections.emptySet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;
import org.apache.cxf.rs.security.oidc.common.IdToken;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.apache.cxf.rs.security.oidc.rp.OidcSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(Priorities.AUTHORIZATION)
public class GSuiteGroupAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GSuiteGroupAuthorizationFilter.class);
    private final Supplier<Set<String>> externalUsersCache;
    private final Supplier<Set<String>> adminUsersCache;
    private final GSuiteDirectoryService gsuiteDirService;


    public GSuiteGroupAuthorizationFilter(final GSuiteDirectoryService gsuiteDirService, Configuration config,
            AppConfiguration appConfig) {

        this.gsuiteDirService = gsuiteDirService;
        this.externalUsersCache = Suppliers.memoizeWithExpiration(
                () -> appConfig.getExternalAccountsGroup().map(g -> getExternalGroupMembers(g)).orElse(emptySet()),
                15, TimeUnit.MINUTES);
        this.adminUsersCache = Suppliers.memoizeWithExpiration(
                () -> getExternalGroupMembers(config.getAdminGroup()),
                15, TimeUnit.MINUTES);
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        OidcSecurityContext secCtx = (OidcSecurityContext) requestContext.getSecurityContext();
        OidcClientTokenContext tokenCtx = secCtx.getOidcContext();
        IdToken idToken = tokenCtx.getIdToken();
        String email = idToken.getEmail();
        String userDomain = idToken.getStringProperty("hd");
        String appDomain = gsuiteDirService.getDomainName();
        if (appDomain == null) {
            throw serverError(SERVICE_UNAVAILABLE, "E002", "Service not configured!");
        }

        boolean internal = gsuiteDirService.getDomainName().equalsIgnoreCase(userDomain);
        boolean external = false;
        Set<String> roles = new HashSet<>();
        if (internal) {
            roles.add(AuthzRole.INTERNAL);
        } else if (externalUsersCache.get().contains(email)) {
            roles.add(AuthzRole.EXTERNAL);
            external = true;
        }
        if (adminUsersCache.get().contains(email)) {
            roles.add(AuthzRole.ADMIN);
        }
        if (internal || external) {
        } else {
            log.error("Unauthorized access from {}", userDomain);
            ServerError err = new ServerError("E001", "Sorry you are not allowed to enter this site");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(err).type(MediaType.APPLICATION_JSON).build());
        }
    }


    private Set<String> getExternalGroupMembers(String groupName) {
        Set<String> result = emptySet();
        try {
            GroupMembership membership = gsuiteDirService.getGroupMembers(groupName);
            result = membership.getMembers() == null ? Collections.emptySet()
                    : membership.getMembers().stream().map(m -> m.getEmail()).collect(Collectors.toSet());
        } catch (ResourceNotFoundException e) {
            log.warn("Group for external accounts {} does not exists", groupName);
        } catch (NoPrivateKeyException e) {
            throw serverError(SERVICE_UNAVAILABLE, "E002", "Service not configured!", e);
        }
        return result;
    }
}

package camp.xit.identity.services.rest.security;

import camp.xit.identity.services.config.Configuration;
import camp.xit.identity.services.google.GSuiteDirectoryService;
import camp.xit.identity.services.google.model.GroupList;
import camp.xit.identity.services.model.ServerError;
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
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Priority(Priorities.AUTHORIZATION)
public class GSuiteGroupAuthorizationFilter implements ContainerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(GSuiteGroupAuthorizationFilter.class);
    private static final String GSUITE_DOMAIN_PROP = "gsuite.domain";
    private static final String GSUITE_SIGN_UP_GROUPS_PROP = "gsuite.signUp.groups";
    private final GSuiteDirectoryService directoryService;
    private final Configuration config;
    private final Cache<String, Set<String>> groupCache;


    public GSuiteGroupAuthorizationFilter(GSuiteDirectoryService directoryService, Configuration config) {
        this.directoryService = directoryService;
        this.config = config;
        this.groupCache = new Cache2kBuilder<String, Set<String>>() {
        }.expireAfterWrite(15, TimeUnit.MINUTES)
                .loader((key) -> {
                    GroupList list = this.directoryService.getGroups(key);
                    Set<String> result = list.getGroups() != null
                            ? list.getGroups().stream().map(g -> g.getEmail()).collect(Collectors.toSet())
                            : Collections.emptySet();
                    return result;
                }).build();
    }


    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        OidcClientTokenContext ctx = (OidcClientTokenContext) JAXRSUtils.getCurrentMessage().getContent(ClientTokenContext.class);
        IdToken idToken = ctx.getIdToken();
        String subject = idToken.getSubject();
        String hdParam = idToken.getStringProperty("hd");
        Set<String> allowGroups = config.getSet(GSUITE_SIGN_UP_GROUPS_PROP);
        boolean fromGsuite = config.get(GSUITE_DOMAIN_PROP).equalsIgnoreCase(hdParam);
        allowGroups.retainAll(groupCache.get(subject));
        boolean memberOfAllowedGroup = !allowGroups.isEmpty();
        if (!fromGsuite || memberOfAllowedGroup) {
            log.error("Unauthorized access from {}", hdParam);
            ServerError err = new ServerError("E001", "Sorry you are not allowed to exit camp");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity(err).type(MediaType.APPLICATION_JSON).build());
        }
    }
}

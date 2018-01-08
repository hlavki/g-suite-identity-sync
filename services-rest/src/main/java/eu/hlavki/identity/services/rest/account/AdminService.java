package eu.hlavki.identity.services.rest.account;

import eu.hlavki.identity.services.rest.config.ConfigurationImpl;
import eu.hlavki.identity.services.rest.model.ServerError;
import eu.hlavki.identity.services.sync.AccountSyncService;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.hlavki.identity.services.rest.config.Configuration;
import eu.hlavki.identity.services.ldap.LdapSystemException;

@Path("admin")
public class AdminService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    @Context
    private OidcClientTokenContext oidcContext;
    private final Configuration config;
    private final AccountSyncService syncService;


    public AdminService(Configuration config, AccountSyncService syncService) {
        this.config = config;
        this.syncService = syncService;
        configure();
    }


    private void configure() {
        log.info("Configuring AdminService ...");
    }


    @Path("sync/groups")
    @PUT
    public Response synchronizeGroups() {
        Response.ResponseBuilder response;
        try {
            syncService.synchronizeAllGroups();
            response = Response.ok();
        } catch (LdapSystemException e) {
            log.error("Can't synchronize groups", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @Path("sync/users")
    @PUT
    public Response synchronizeUsers() {
        Response.ResponseBuilder response;
        try {
            syncService.synchronizeGSuiteUsers();
            response = Response.ok();
        } catch (LdapSystemException e) {
            log.error("Can't synchronize users", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @Override
    public void handleEvent(Event event) {
        if (ConfigurationImpl.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }
}

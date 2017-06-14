package camp.xit.identity.services.rest.account;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.config.Configuration;
import camp.xit.identity.services.model.ServerError;
import camp.xit.identity.services.sync.AccountSyncService;
import com.unboundid.ldap.sdk.LDAPException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import org.apache.cxf.rs.security.oidc.rp.OidcClientTokenContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("admin")
public class AdminService implements EventHandler {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    @Context
    private OidcClientTokenContext oidcContext;
    private final AppConfiguration config;
    private final AccountSyncService syncService;


    public AdminService(AppConfiguration config, AccountSyncService syncService) {
        this.config = config;
        this.syncService = syncService;
        configure();
    }


    private void configure() {
        log.info("Configuring AdminService ...");
    }


    @Path("sync")
    @PUT
    public Response synchronizeGroups() {
        Response.ResponseBuilder response;
        try {
            syncService.synchronizeAllGroups();
            response = Response.ok();
        } catch (LDAPException e) {
            log.error("Can't synchronize groups", e);
            response = ServerError.toResponse("LDAP_ERR", e);
        }
        return response.build();
    }


    @Override
    public void handleEvent(Event event) {
        if (Configuration.TOPIC_CHANGE.equals(event.getTopic())) {
            configure();
        }
    }
}

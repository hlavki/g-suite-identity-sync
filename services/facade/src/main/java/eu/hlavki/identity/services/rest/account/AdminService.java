package eu.hlavki.identity.services.rest.account;

import eu.hlavki.identity.services.ldap.LdapSystemException;
import eu.hlavki.identity.services.rest.model.ServerError;
import eu.hlavki.identity.services.sync.AccountSyncService;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("admin")
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    private final AccountSyncService syncService;


    public AdminService(AccountSyncService syncService) {
        this.syncService = syncService;
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
}

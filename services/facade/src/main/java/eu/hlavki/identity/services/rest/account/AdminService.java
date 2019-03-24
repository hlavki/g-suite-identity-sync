package eu.hlavki.identity.services.rest.account;

import eu.hlavki.identity.services.sync.AccountSyncService;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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
    public void synchronizeGroups() {
        syncService.synchronizeAllGroups();
    }


    @Path("sync/users")
    @PUT
    public void synchronizeUsers() {
        syncService.synchronizeGSuiteUsers();
    }
}

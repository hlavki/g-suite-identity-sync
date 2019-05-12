package eu.hlavki.identity.services.rest.service;

import eu.hlavki.identity.services.google.PushNotificationService;
import eu.hlavki.identity.services.rest.model.PushNotificationStatus;
import eu.hlavki.identity.services.sync.AccountSyncService;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("admin")
public class AdminService {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);
    private final AccountSyncService syncService;
    private final PushNotificationService pushService;


    public AdminService(AccountSyncService syncService, PushNotificationService pushService) {
        this.syncService = syncService;
        this.pushService = pushService;
    }


    @PUT
    @Path("push/enable")
    public void enablePushNotifications(@QueryParam("hostname") String hostname) {
        pushService.enablePushNotifications(hostname);
    }


    @PUT
    @Path("push/disable")
    public void disablePushNotifications() {
        pushService.disablePushNotifications();
    }


    @GET
    @Path("push/status")
    public PushNotificationStatus getPushNotificationStatus() {
        return new PushNotificationStatus(pushService.isEnabled());
    }


    @PUT
    @Path("sync/groups")
    public void synchronizeGroups() {
        syncService.synchronizeAllGroups();
    }


    @PUT
    @Path("sync/users")
    public void synchronizeUsers() {
        syncService.synchronizeGSuiteUsers();
    }
}

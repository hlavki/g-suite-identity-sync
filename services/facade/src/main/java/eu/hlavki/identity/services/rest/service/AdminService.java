package eu.hlavki.identity.services.rest.service;

import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import eu.hlavki.identity.services.google.PushNotificationService;
import eu.hlavki.identity.services.google.model.GSuiteGroup;
import eu.hlavki.identity.services.rest.model.GeneralSettings;
import eu.hlavki.identity.services.rest.model.PushNotificationStatus;
import static eu.hlavki.identity.services.rest.security.AuthzRole.ADMIN;
import eu.hlavki.identity.services.sync.AccountSyncService;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("admin")
@RolesAllowed(ADMIN)
public class AdminService {

    private final AccountSyncService syncService;
    private final PushNotificationService pushService;
    private final GSuiteDirectoryService gsuiteDirService;
    private final AppConfiguration appConfig;


    public AdminService(AccountSyncService syncService, PushNotificationService pushService,
            GSuiteDirectoryService gsuiteDirService, AppConfiguration appConfig) {
        this.syncService = syncService;
        this.pushService = pushService;
        this.gsuiteDirService = gsuiteDirService;
        this.appConfig = appConfig;
    }


    @GET
    @Path("google/groups")
    public List<GSuiteGroup> getGSuiteGroups() {
        return gsuiteDirService.getAllGroups().getGroups();
    }


    @GET
    @Path("general-settings")
    public GeneralSettings getGeneralSettings() {
        String externalGroup = appConfig.getExternalAccountsGroup()
                .map(gsuiteDirService::completeGroupEmail).orElse(null);
        return new GeneralSettings(externalGroup);
    }


    @PUT
    @Path("general-settings")
    public void setGeneralSettings(GeneralSettings settings) {
        appConfig.setExternalAccountsGroup(settings.getExternalAccountsGroup());
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

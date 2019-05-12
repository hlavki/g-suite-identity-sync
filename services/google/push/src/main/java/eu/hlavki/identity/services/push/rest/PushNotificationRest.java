package eu.hlavki.identity.services.push.rest;

import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.ldap.LdapSystemException;
import eu.hlavki.identity.services.push.config.Configuration;
import eu.hlavki.identity.services.push.model.AuditRecord;
import eu.hlavki.identity.services.push.model.AuditRecord.Event;
import eu.hlavki.identity.services.sync.AccountSyncService;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class PushNotificationRest {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationRest.class);
    private final Configuration config;
    private final AccountSyncService syncService;

    public PushNotificationRest(Configuration config, AccountSyncService syncService) {
        this.config = config;
        this.syncService = syncService;
    }

    @POST
    @Path("notify")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void notifyChanges() {
        log.info("Init request from Google push notifications!");
    }

    @POST
    @Path("notify")
    @Consumes(MediaType.APPLICATION_JSON)
    public void notifyChanges(AuditRecord record) {
        for (Event evt : record.getEvents()) {
            log.info("Incoming event {} of type {}", evt.getName(), evt.getType());
            switch (evt.getType()) {
            case GROUP_SETTINGS:
                switch (evt.getName()) {
                case "DELETE_GROUP":
                    deleteGroup(evt);
                    break;
                default:
                    syncGroup(evt);
                    break;
                }
                break;
            case USER_SETTINGS:
                switch (evt.getName()) {
                case "DELETE_USER":
                    deleteUser(evt);
                    break;
                default:
                    log.info("Event {} of type {} is not relevant for me!", evt.getName(), evt.getType());
                    break;
                }
                break;
            default:
                log.info("Event {} of type {} is not relevant for me!", evt.getName(), evt.getType());
            }
        }
    }

    private void syncGroup(Event evt) {
        try {
            syncService.synchronizeGroup(getGroupEmail(evt));
            syncService.cleanExternalUsers();
        } catch (LdapSystemException | ResourceNotFoundException e) {
            log.error("Cannot process event " + evt + " because of error!", e);
        }
    }

    private void deleteGroup(Event evt) {
        try {
            syncService.removeGroup(getGroupEmail(evt));
        } catch (LdapSystemException e) {
            log.error("Cannot remove group from event " + evt + " because of error!", e);
        }
    }

    private void deleteUser(Event evt) {
        try {
            syncService.removeUserByEmail(getUserEmail(evt));
        } catch (LdapSystemException e) {
            log.error("Cannot remove group from event " + evt + " because of error!", e);
        }
    }

    private String getGroupEmail(Event evt) {
        return evt.getParameters().stream()
                .filter(p -> p.getName().equals("GROUP_EMAIL"))
                .findAny().map(p -> p.getValue()).orElseThrow();
    }

    private String getUserEmail(Event evt) {
        return evt.getParameters().stream()
                .filter(p -> p.getName().equals("USER_EMAIL"))
                .findAny().map(p -> p.getValue()).orElseThrow();
    }
}

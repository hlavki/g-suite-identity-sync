package eu.hlavki.identity.services.push.rest;

import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.ldap.LdapSystemException;
import eu.hlavki.identity.services.push.model.AuditRecord;
import eu.hlavki.identity.services.push.model.AuditRecord.Event;
import eu.hlavki.identity.services.sync.AccountSyncService;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import eu.hlavki.identity.services.push.config.Configuration;

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
        Set<String> groups = record.getEvents().stream()
                .filter(this::isGroupEvent)
                .map(this::getGroupName).collect(Collectors.toSet());

        log.info("Refreshing groups {}", groups);
        for (String group : groups) {
            try {
                syncService.synchronizeGroup(group);
                log.info("Group {} synchronized", group);
            } catch (LdapSystemException | ResourceNotFoundException e) {
                log.warn("Cannot synchronize group " + group, e);
            }
        }
    }

    private boolean isGroupEvent(Event evt) {
        return evt.getType().equals("GROUP_SETTINGS");
    }

    private String getGroupName(Event evt) {
        return evt.getParameters().stream().filter(p -> p.getName().equals("GROUP_EMAIL")).findAny().map(p -> p.getValue()).orElseThrow();
    }
}

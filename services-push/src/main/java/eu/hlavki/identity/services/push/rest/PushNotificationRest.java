package eu.hlavki.identity.services.push.rest;

import eu.hlavki.identity.services.push.config.Config;
import eu.hlavki.identity.services.push.model.AuditRecord;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class PushNotificationRest {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationRest.class);
    private final Config config;

    public PushNotificationRest(Config config) {
        this.config = config;
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
    public void notifyChanges(AuditRecord record
    ) {
        if (record == null) {
            log.info("Record is empty!");
        } else {
            log.info("{} event received ", record.getEvents().size());
        }
    }
}

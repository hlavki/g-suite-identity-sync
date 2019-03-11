package eu.hlavki.identity.services.push.rest;

import eu.hlavki.identity.services.push.config.Config;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
public class PushNotificationRest {

    private final Config config;

    public PushNotificationRest(Config config) {
        this.config = config;
    }

    @POST
    @Path("notify")
    public void notifyChanges() {
    }
}

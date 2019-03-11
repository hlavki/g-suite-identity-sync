package eu.hlavki.identity.services.push;

import eu.hlavki.identity.services.push.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    private final Config config;

    public PushNotificationService(Config config) {
        this.config = config;
    }

    public void configure() {
        log.info("Push notifications are {}", config.isEnabled() ? "enabled" : "disabled");
    }
}

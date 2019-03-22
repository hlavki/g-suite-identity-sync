package eu.hlavki.identity.services.google.client;

import eu.hlavki.identity.services.google.PushNotificationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "google", name = "watch-domain", description = "Watch domain")
public class WatchDomainCommand implements Action {

    @Argument(index = 0, name = "hostname", description = "Hostname of REST service where google send notifications", required = true, multiValued = false)
    String hostname;

    @Reference
    PushNotificationService pushService;

    @Override
    public Object execute() throws Exception {
        pushService.enablePushNotifications(hostname);
        return null;
    }
}

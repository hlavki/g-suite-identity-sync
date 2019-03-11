package eu.hlavki.identity.services.google.client;

import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "google", name = "watch-domain", description = "Watch domain")
public class WatchDomainCommand implements Action {

    @Reference
    GSuiteDirectoryService gsuiteService;

    @Override
    public Object execute() throws Exception {
        gsuiteService.enablePushNotifications();
        return null;
    }
}

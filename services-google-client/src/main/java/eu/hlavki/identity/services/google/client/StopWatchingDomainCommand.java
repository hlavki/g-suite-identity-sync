package eu.hlavki.identity.services.google.client;

import eu.hlavki.identity.services.google.GSuiteDirectoryService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "google", name = "stop-watching-domain", description = "Stop watching domain")
public class StopWatchingDomainCommand implements Action {

    @Reference
    GSuiteDirectoryService gsuiteService;

    @Override
    public Object execute() throws Exception {
        gsuiteService.disablePushNotifications();
        return null;
    }
}

package eu.hlavki.identity.services.google.client;

import eu.hlavki.identity.services.google.PushNotificationService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "google", name = "stop-channel", description = "Stop channel")
public class StopPushChannel implements Action {

    @Argument(index = 0, name = "id", description = "Channel id", required = true, multiValued = false)
    String id;

    @Argument(index = 1, name = "resourceId", description = "Resource id", required = true, multiValued = false)
    String resourceId;

    @Reference
    PushNotificationService pushService;

    @Override
    public Object execute() throws Exception {
        pushService.stopPushChannel(id, resourceId);
        return null;
    }
}

package eu.hlavki.identity.services.sync.client;

import eu.hlavki.identity.services.sync.AccountSyncService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "identity", name = "synchronize-users-attr", description = "Synchronize G Suite user attributes")
public class SyncUserAttributesCommand implements Action {

    @Reference
    AccountSyncService syncService;

    @Override
    public Object execute() throws Exception {
        syncService.synchronizeGSuiteUsers();
        return null;
    }
}

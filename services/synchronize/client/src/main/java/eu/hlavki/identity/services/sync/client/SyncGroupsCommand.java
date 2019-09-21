package eu.hlavki.identity.services.sync.client;

import eu.hlavki.identity.services.sync.AccountSyncService;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;

@Service
@Command(scope = "identity", name = "synchronize-groups", description = "Synchronize G Suite groups to LDAP")
public class SyncGroupsCommand implements Action {

    @Reference
    AccountSyncService syncService;

    @Override
    public Object execute() throws Exception {
        syncService.synchronizeAllGroups();
        return null;
    }
}

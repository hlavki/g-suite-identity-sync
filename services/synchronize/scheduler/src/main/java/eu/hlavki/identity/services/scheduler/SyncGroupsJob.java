package eu.hlavki.identity.services.scheduler;

import eu.hlavki.identity.services.ldap.LdapSystemException;
import eu.hlavki.identity.services.sync.AccountSyncService;
import org.apache.karaf.scheduler.Job;
import org.apache.karaf.scheduler.JobContext;
import org.apache.karaf.scheduler.Scheduler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(immediate = true, property = {
    Scheduler.PROPERTY_SCHEDULER_EXPRESSION + "=0 0 * * * ?",
    Scheduler.PROPERTY_SCHEDULER_NAME + "=SyncGroups",
    Scheduler.PROPERTY_SCHEDULER_CONCURRENT + ":Boolean=false"})
public class SyncGroupsJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(SyncGroupsJob.class);

    @Reference
    private AccountSyncService syncService;

    @Override
    public void execute(JobContext context) {
        try {
            LOG.info("Running scheduler for synchronizing all group");
            syncService.synchronizeAllGroups();
        } catch (LdapSystemException e) {
            LOG.error("Cannot synchronize groups with scheduler", e);
        }
    }
}

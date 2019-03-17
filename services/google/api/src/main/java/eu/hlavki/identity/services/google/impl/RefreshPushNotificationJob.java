package eu.hlavki.identity.services.google.impl;

import eu.hlavki.identity.services.google.PushNotificationService;
import org.apache.karaf.scheduler.Job;
import org.apache.karaf.scheduler.JobContext;

public class RefreshPushNotificationJob implements Job {

    private final PushNotificationService pushService;


    public RefreshPushNotificationJob(PushNotificationService pushService) {
        this.pushService = pushService;
    }


    @Override
    public void execute(JobContext jc) {
        pushService.refreshPushNotifications();
    }
}

package eu.hlavki.identity.services.google;

public interface PushNotificationService {

    /**
     * Enable push notification. Set hostname for push service
     *
     * @param hostname
     */
    void enablePushNotifications(String hostname);


    void disablePushNotifications();


    void refreshPushNotifications();


    void stopPushChannel(String id, String resourceId);


    boolean isEnabled();
}

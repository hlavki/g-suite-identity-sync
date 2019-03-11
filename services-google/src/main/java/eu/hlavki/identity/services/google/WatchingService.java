package eu.hlavki.identity.services.google;

import eu.hlavki.identity.services.google.impl.NotificationType;
import eu.hlavki.identity.services.google.model.Watching;
import java.util.Optional;
import java.util.Set;

public interface WatchingService {

    /**
     * Add or Update push notification
     *
     * @param notify
     */
    void addNotification(Watching notification);


    /**
     * Remove push notification
     *
     * @param notify
     */
    void removeNotification(Watching notification);


    Set<Watching> getAllNotifications();


    /**
     * Return all push notifications that expires in defined time. Default time is 1 hour.
     *
     * @return push notifications that expire soon
     */
    Set<Watching> getNotificationsBeforeExpiration();


    /**
     * Get one or none push notification for selected type
     *
     * @param type
     * @return
     */
    Optional<Watching> getNotificationByType(NotificationType type);
}

package eu.hlavki.identity.services.google.impl;

import eu.hlavki.identity.services.google.config.Configuration;
import eu.hlavki.identity.services.google.model.Watching;
import eu.hlavki.identity.services.google.model.Watchings;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;
import eu.hlavki.identity.services.google.WatchingService;

public class WatchingServiceImpl implements WatchingService {

    private static final Logger log = LoggerFactory.getLogger(WatchingServiceImpl.class);

    private final Configuration config;
    private Set<Watching> notifications;


    public WatchingServiceImpl(Configuration config) {
        this.config = config;
        if (config.getPushWatchingsFile().exists()) {
            try {
                this.notifications = read();
            } catch (JAXBException e) {
                log.error("Cannot read watchings!", e);
            }
        } else {
            notifications = new HashSet<>();
        }
    }


    @Override
    public void addNotification(Watching notification) {
        notifications.add(notification);
        store();
    }


    @Override
    public void removeNotification(Watching notification) {
        notifications.remove(notification);
        store();
    }


    @Override
    public Set<Watching> getAllNotifications() {
        return notifications;
    }


    @Override
    public Set<Watching> getNotificationsBeforeExpiration() {
        Duration hour = Duration.ofHours(1);
        return notifications.stream().filter(w -> w.expiresIn(hour)).collect(Collectors.toSet());
    }


    @Override
    public Optional<Watching> getNotificationByType(NotificationType type) {
        return notifications.stream().filter(w -> w.getType().equals(type)).findAny();
    }


    private Set<Watching> read() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Watchings.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        //We had written this file in marshalling example
        Watchings ntfs = (Watchings) jaxbUnmarshaller.unmarshal(config.getPushWatchingsFile());
        return ntfs.getPushNotifications();
    }


    private void store() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Watchings.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            //Marshal the employees list in file
            jaxbMarshaller.marshal(new Watchings(notifications), config.getPushWatchingsFile());
        } catch (JAXBException e) {
            log.error("Cannot store watchings", e);
        }
    }
}

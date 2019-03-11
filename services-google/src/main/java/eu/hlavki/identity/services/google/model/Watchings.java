package eu.hlavki.identity.services.google.model;

import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Watchings {

    @XmlElement(name = "notification")
    private Set<Watching> pushNotifications;


    public Watchings() {
    }


    public Watchings(Set<Watching> pushNotifications) {
        this.pushNotifications = pushNotifications;
    }


    public Set<Watching> getPushNotifications() {
        if (pushNotifications == null) this.pushNotifications = new HashSet<>();
        return pushNotifications;
    }


    public void setPushNotifications(Set<Watching> pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
}

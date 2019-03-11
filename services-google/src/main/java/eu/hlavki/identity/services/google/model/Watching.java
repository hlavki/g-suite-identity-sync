package eu.hlavki.identity.services.google.model;

import eu.hlavki.identity.services.google.impl.NotificationType;
import java.time.Duration;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Watching {

    @EqualsAndHashCode.Include
    @XmlElement
    private String id;
    @XmlElement
    private String kind;
    @XmlElement
    private String resourceId;
    @XmlElement
    private String resourceUri;
    @XmlElement
    private String token;
    @XmlElement
    private Long expiration;
    @XmlElement
    private NotificationType type;

    public boolean expiresIn(Duration bestBefore) {
        return expiration - System.currentTimeMillis() < bestBefore.toMillis();
    }
}

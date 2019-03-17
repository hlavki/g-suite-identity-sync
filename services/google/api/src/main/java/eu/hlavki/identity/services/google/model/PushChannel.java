package eu.hlavki.identity.services.google.model;

import java.time.Duration;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
@XmlRootElement(name = "channel")
@XmlAccessorType(XmlAccessType.FIELD)
public class PushChannel {

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


    public boolean expiresIn(Duration bestBefore) {
        return expiration - System.currentTimeMillis() < bestBefore.toMillis();
    }
}

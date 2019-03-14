package eu.hlavki.identity.services.push.model;

import java.time.LocalDateTime;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class AuditRecord {

    private String kind;
    private Id id;
    private String etag;
    private String ipAddress;
    private List<Event> events;

    @Getter
    @Setter
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Id {

        @XmlJavaTypeAdapter(LocalDateTimeAdapter.class)
        private LocalDateTime time;
        private Long uniqueQualifier;
        private String applicationName;
        private String customerId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Actor {

        private String callerType;
        private String email;
        private String profileId;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Event {

        private String type;
        private String name;
        private Long profileId;
        private List<Param> parameters;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Param {

        private String name;
        private String value;
        private Integer intValue;
        private Boolean boolValue;
    }

}

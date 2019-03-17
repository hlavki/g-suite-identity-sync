package eu.hlavki.identity.services.google.model;

import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
public class StopPushChannel {

    private String id;
    private String resourceId;


    public StopPushChannel(PushChannel watching) {
        this.id = watching.getId();
        this.resourceId = watching.getResourceId();
    }
}

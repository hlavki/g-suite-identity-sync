package eu.hlavki.identity.services.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement
public class GoogleSettingsData {

    private String domain;
    private UserInfo userInfo;
    private String serviceAccountJson;


    public GoogleSettingsData(String domain, UserInfo userInfo) {
        this.domain = domain;
        this.userInfo = userInfo;
    }
}

package eu.hlavki.identity.services.rest.model;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement
public class UserInfo {

    private String name;
    private String email;
    private URI imageUri;
    private boolean amAdmin;


    public UserInfo(String name, String email) {
        this.name = name;
        this.email = email;
    }


    public UserInfo(String name, String email, boolean amAdmin, URI imageUri) {
        this.name = name;
        this.email = email;
        this.amAdmin = amAdmin;
        this.imageUri = imageUri;
    }
}

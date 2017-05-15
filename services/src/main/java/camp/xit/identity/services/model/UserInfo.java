package camp.xit.identity.services.model;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserInfo {

    private String name;
    private String email;
    private URI imageUri;

    public UserInfo() {
    }


    public UserInfo(String name, String email, URI imageUri) {
        this.name = name;
        this.email = email;
        this.imageUri = imageUri;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public URI getImageUri() {
        return imageUri;
    }


    public void setImageUri(URI imageUri) {
        this.imageUri = imageUri;
    }
}

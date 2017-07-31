package eu.hlavki.identity.services.model;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserInfo {

    private String name;
    private String email;
    private URI imageUri;
    private boolean amAdmin;


    public UserInfo() {
    }


    public UserInfo(String name, String email, boolean amAdmin, URI imageUri) {
        this.name = name;
        this.email = email;
        this.amAdmin = amAdmin;
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


    public boolean isAmAdmin() {
        return amAdmin;
    }


    public void setAmAdmin(boolean amAdmin) {
        this.amAdmin = amAdmin;
    }
}

package camp.xit.auth.services.rest.user;

import java.net.URI;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDetail {

    public enum Role {
        INTERNAL, EXTERNAL
    }

    private String givenName;
    private String familyName;
    private String name;
    private String email;
    private URI profilePicture;
    private boolean emailVerified;
    private Role role;


    public UserDetail() {
    }


    public String getGivenName() {
        return givenName;
    }


    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }


    public String getFamilyName() {
        return familyName;
    }


    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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


    public URI getProfilePicture() {
        return profilePicture;
    }


    public void setProfilePicture(URI profilePicture) {
        this.profilePicture = profilePicture;
    }


    public boolean isEmailVerified() {
        return emailVerified;
    }


    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }


    public Role getRole() {
        return role;
    }


    public void setRole(Role role) {
        this.role = role;
    }

}

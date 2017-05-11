package camp.xit.auth.services.model;

import camp.xit.auth.services.model.PrepareAccountData.Role;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountInfo {

    private String username;
    private String email;
    private String subject;
    private String givenName;
    private String familyName;
    private String name;
    private Role role;


    public AccountInfo() {
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getSubject() {
        return subject;
    }


    public void setSubject(String subject) {
        this.subject = subject;
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


    public Role getRole() {
        return role;
    }


    public void setRole(Role role) {
        this.role = role;
    }
}

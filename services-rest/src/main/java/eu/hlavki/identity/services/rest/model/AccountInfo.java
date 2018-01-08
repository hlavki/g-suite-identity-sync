package eu.hlavki.identity.services.rest.model;

import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AccountInfo {

    private String username;
    private Set<String> emails;
    private String subject;
    private String givenName;
    private String familyName;
    private String name;
    private Role role;
    private boolean syncGsuitePassword;


    public AccountInfo() {
    }


    public AccountInfo(String username, Set<String> emails, String subject, String givenName,
        String familyName, String name, Role role, boolean syncGsuitePassword) {
        this.username = username;
        this.emails = emails;
        this.subject = subject;
        this.givenName = givenName;
        this.familyName = familyName;
        this.name = name;
        this.role = role;
    }


    public Set<String> getEmails() {
        return emails;
    }


    public void setEmails(Set<String> emails) {
        this.emails = emails;
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


    public boolean isSyncGsuitePassword() {
        return syncGsuitePassword;
    }


    public void setSyncGsuitePassword(boolean syncGsuitePassword) {
        this.syncGsuitePassword = syncGsuitePassword;
    }
}

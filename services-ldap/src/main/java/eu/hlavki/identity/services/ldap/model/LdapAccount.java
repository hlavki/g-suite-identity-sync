package eu.hlavki.identity.services.ldap.model;

import java.util.Set;

public class LdapAccount {

    public enum Role {
        EXTERNAL, INTERNAL
    }

    private String givenName;
    private String familyName;
    private String name;
    private String username;
    private String subject;
    private Set<String> emails;
    private String password;
    private Role role;


    public LdapAccount() {
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


    public Set<String> getEmails() {
        return emails;
    }


    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public Role getRole() {
        return role;
    }


    public void setRole(Role role) {
        this.role = role;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("givenName=").append(givenName).
            append(", familyName=").append(familyName).
            append(", name=").append(name).
            append(", username=").append(username).
            append(", subject=").append(subject).
            append(", emails=").append(emails).
            append(", role=").append(role).append("]");
        return sb.toString();
    }
}

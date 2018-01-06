package eu.hlavki.identity.plugin.api.model;

import java.util.Set;

public class CreatedUser {

    String givenName;
    String familyName;
    String name;
    String username;
    String subject;
    Set<String> emails;
    Role role;


    public CreatedUser() {
    }


    public CreatedUser(String givenName, String familyName, String name, String username,
        String subject, Set<String> emails, Role role) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.name = name;
        this.username = username;
        this.subject = subject;
        this.emails = emails;
        this.role = role;
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


    public Role getRole() {
        return role;
    }


    public void setRole(Role role) {
        this.role = role;
    }
}

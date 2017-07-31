package eu.hlavki.identity.services.model;

import java.net.URI;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PrepareAccountData {

    public enum Role {
        INTERNAL, EXTERNAL
    }

    public static class Group {

        private String name;
        private String email;


        public Group() {
        }


        public Group(String name, String email) {
            this.name = name;
            this.email = email;
        }


        public String getEmail() {
            return email;
        }


        public void setEmail(String email) {
            this.email = email;
        }


        public String getName() {
            return name;
        }


        public void setName(String name) {
            this.name = name;
        }


        public static Group map(eu.hlavki.identity.services.google.model.GSuiteGroup group) {
            return new Group(group.getName(), group.getEmail());
        }
    }

    private String givenName;
    private String familyName;
    private String name;
    private Set<String> emails;
    private String email;
    private URI profilePicture;
    private boolean emailVerified;
    private Role role;
    private boolean saveGSuitePassword;
    private List<Group> groups;


    public PrepareAccountData() {
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


    public Set<String> getEmails() {
        return emails;
    }


    public void setEmails(Set<String> emails) {
        this.emails = emails;
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


    public boolean isSaveGSuitePassword() {
        return saveGSuitePassword;
    }


    public void setSaveGSuitePassword(boolean saveGSuitePassword) {
        this.saveGSuitePassword = saveGSuitePassword;
    }


    public List<Group> getGroups() {
        return groups;
    }


    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}

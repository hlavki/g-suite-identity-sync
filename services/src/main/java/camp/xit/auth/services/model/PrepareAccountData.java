package camp.xit.auth.services.model;

import java.net.URI;
import java.util.List;
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


        public static Group map(camp.xit.auth.services.google.model.Group group) {
            return new Group(group.getName(), group.getEmail());
        }
    }

    public static class EmailAddress {

        private String email;
        private boolean primary;
        private boolean verified;


        public EmailAddress() {
        }


        public EmailAddress(String email, boolean primary, boolean verified) {
            this.email = email;
            this.primary = primary;
            this.verified = verified;
        }


        public String getEmail() {
            return email;
        }


        public void setEmail(String email) {
            this.email = email;
        }


        public boolean isVerified() {
            return verified;
        }


        public void setVerified(boolean verified) {
            this.verified = verified;
        }


        public boolean isPrimary() {
            return primary;
        }


        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

    }

    private String givenName;
    private String familyName;
    private String name;
    private List<EmailAddress> emails;
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


    public List<EmailAddress> getEmails() {
        return emails;
    }


    public void setEmails(List<EmailAddress> emails) {
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

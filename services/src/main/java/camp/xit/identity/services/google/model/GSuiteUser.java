package camp.xit.identity.services.google.model;

import java.util.Date;
import java.util.List;

/**
 * See <a href=https://developers.google.com/admin-sdk/directory/v1/reference/users#resource">API
 * Reference</a>
 *
 * @author Michal Hlavac
 */
public class GSuiteUser {

    public static class Name {

        private String givenName;
        private String familyName;
        private String fullName;


        public Name() {
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


        public String getFullName() {
            return fullName;
        }


        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
    }

    public static class Email {

        private String address;
        private String customType;
        private String type;


        public Email() {
        }


        public String getAddress() {
            return address;
        }


        public void setAddress(String address) {
            this.address = address;
        }


        public String getCustomType() {
            return customType;
        }


        public void setCustomType(String customType) {
            this.customType = customType;
        }


        public String getType() {
            return type;
        }


        public void setType(String type) {
            this.type = type;
        }
    }

    private String kind;
    private String id;
    private String etag;
    private String primaryEmail;
    private Name name;
    private boolean isAdmin;
    private boolean isDelegatedAdmin;
    private Date lastLoginTime;
    private Date creationTime;
    private Date deletionTime;
    private boolean agreedToTerms;
    private String password;
    private String hashFunction;
    private boolean suspended;
    private String suspensionReason;
    private boolean changePasswordAtNextLogin;
    private boolean ipWhitelisted;
    private List<Email> emails;
    private List<String> aliases;


    public GSuiteUser() {
    }


    public String getKind() {
        return kind;
    }


    public void setKind(String kind) {
        this.kind = kind;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getEtag() {
        return etag;
    }


    public void setEtag(String etag) {
        this.etag = etag;
    }


    public String getPrimaryEmail() {
        return primaryEmail;
    }


    public void setPrimaryEmail(String primaryEmail) {
        this.primaryEmail = primaryEmail;
    }


    public Name getName() {
        return name;
    }


    public void setName(Name name) {
        this.name = name;
    }


    public boolean isIsAdmin() {
        return isAdmin;
    }


    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }


    public boolean isIsDelegatedAdmin() {
        return isDelegatedAdmin;
    }


    public void setIsDelegatedAdmin(boolean isDelegatedAdmin) {
        this.isDelegatedAdmin = isDelegatedAdmin;
    }


    public Date getLastLoginTime() {
        return lastLoginTime;
    }


    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }


    public Date getCreationTime() {
        return creationTime;
    }


    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }


    public Date getDeletionTime() {
        return deletionTime;
    }


    public void setDeletionTime(Date deletionTime) {
        this.deletionTime = deletionTime;
    }


    public boolean isAgreedToTerms() {
        return agreedToTerms;
    }


    public void setAgreedToTerms(boolean agreedToTerms) {
        this.agreedToTerms = agreedToTerms;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getHashFunction() {
        return hashFunction;
    }


    public void setHashFunction(String hashFunction) {
        this.hashFunction = hashFunction;
    }


    public boolean isSuspended() {
        return suspended;
    }


    public void setSuspended(boolean suspended) {
        this.suspended = suspended;
    }


    public String getSuspensionReason() {
        return suspensionReason;
    }


    public void setSuspensionReason(String suspensionReason) {
        this.suspensionReason = suspensionReason;
    }


    public boolean isChangePasswordAtNextLogin() {
        return changePasswordAtNextLogin;
    }


    public void setChangePasswordAtNextLogin(boolean changePasswordAtNextLogin) {
        this.changePasswordAtNextLogin = changePasswordAtNextLogin;
    }


    public boolean isIpWhitelisted() {
        return ipWhitelisted;
    }


    public void setIpWhitelisted(boolean ipWhitelisted) {
        this.ipWhitelisted = ipWhitelisted;
    }


    public List<Email> getEmails() {
        return emails;
    }


    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }


    public List<String> getAliases() {
        return aliases;
    }


    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }


    public final GroupMember toMember() {
        GroupMember result = new GroupMember();
        result.setId(getId());
        result.setEmail(getPrimaryEmail());
        result.setKind(getKind());
        result.setStatus(GroupMember.Status.ACTIVE);
        result.setType(GroupMember.Type.USER);
        return result;
    }
}

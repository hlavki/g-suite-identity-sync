package camp.xit.identity.services.ldap.model;

import camp.xit.identity.services.config.AppConfiguration;
import camp.xit.identity.services.google.model.GSuiteUser;
import camp.xit.identity.services.model.CreateAccountData;
import camp.xit.identity.services.model.PrepareAccountData.Role;
import camp.xit.identity.services.model.UpdateAccountData;
import camp.xit.identity.services.util.AccountUtil;
import java.util.HashSet;
import java.util.Set;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public class LdapAccount {

    String givenName;
    String familyName;
    String name;
    String username;
    String subject;
    Set<String> emails;
    String password;
    Role role;


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


    public final static LdapAccount from(AppConfiguration cfg, UserInfo userInfo, Set<String> emails,
            CreateAccountData createData) {
        LdapAccount account = new LdapAccount();
        account.setSubject(userInfo.getSubject());
        account.setGivenName(userInfo.getGivenName());
        account.setFamilyName(userInfo.getFamilyName());
        account.setName(userInfo.getName());
        account.setPassword(createData.getPassword());
        account.setUsername(createData.getEmail());
        account.setRole(AccountUtil.getAccountRole(cfg, userInfo));
        account.setEmails(emails);
        return account;
    }


    public final static LdapAccount from(AppConfiguration cfg, UserInfo userInfo, Set<String> emails,
            UpdateAccountData updateData) {
        LdapAccount account = new LdapAccount();
        account.setSubject(userInfo.getSubject());
        account.setGivenName(userInfo.getGivenName());
        account.setFamilyName(userInfo.getFamilyName());
        account.setName(userInfo.getName());
        account.setPassword(updateData.getPassword());
        account.setRole(AccountUtil.getAccountRole(cfg, userInfo));
        account.setEmails(emails);
        return account;
    }


    public final static LdapAccount from(GSuiteUser user) {
        LdapAccount account = new LdapAccount();
        account.setSubject(user.getId());
        account.setGivenName(user.getName().getGivenName());
        account.setFamilyName(user.getName().getFamilyName());
        account.setName(user.getName().getFullName());
        account.setRole(Role.INTERNAL);
        Set<String> emails = new HashSet<>(user.getAliases());
        emails.add(user.getPrimaryEmail());
        account.setEmails(emails);
        return account;
    }
}

package eu.hlavki.identity.services.ldap.model;

import eu.hlavki.identity.plugin.api.model.CreatedUser;
import eu.hlavki.identity.services.config.AppConfiguration;
import eu.hlavki.identity.services.google.model.GSuiteUser;
import eu.hlavki.identity.services.model.CreateAccountData;
import eu.hlavki.identity.services.model.Role;
import eu.hlavki.identity.services.model.UpdateAccountData;
import eu.hlavki.identity.services.util.AccountUtil;
import java.util.HashSet;
import java.util.Set;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public class LdapAccount {

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
        Set<String> emails = new HashSet<>();
        emails.add(user.getPrimaryEmail());
        account.setEmails(emails);
        return account;
    }


    public final CreatedUser toCreated() {
        return new CreatedUser(
            getGivenName(),
            getFamilyName(),
            getName(),
            getUsername(),
            getSubject(),
            getEmails(),
            eu.hlavki.identity.plugin.api.model.Role.valueOf(getRole().toString()));
    }
}

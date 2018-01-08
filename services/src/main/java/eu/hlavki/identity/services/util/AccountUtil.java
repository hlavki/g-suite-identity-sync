package eu.hlavki.identity.services.util;

import eu.hlavki.identity.plugin.api.model.CreatedUser;
import eu.hlavki.identity.services.google.model.GSuiteUser;
import eu.hlavki.identity.services.model.AccountInfo;
import eu.hlavki.identity.services.model.Role;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.model.CreateAccountData;
import eu.hlavki.identity.services.model.UpdateAccountData;
import java.util.HashSet;
import java.util.Set;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import eu.hlavki.identity.services.config.Configuration;

public final class AccountUtil {

    private AccountUtil() {
    }


    public static final boolean isInternalAccount(UserInfo info, String gsuiteDomain) {
        return gsuiteDomain.equals(info.getProperty("hd"));
    }


    public static final Role getAccountRole(UserInfo userInfo, String gsuiteDomain) {
        return isInternalAccount(userInfo, gsuiteDomain) ? Role.INTERNAL : Role.EXTERNAL;
    }


    public static final LdapAccount.Role getLdapRole(UserInfo userInfo, String gsuiteDomain) {
        return isInternalAccount(userInfo, gsuiteDomain) ? LdapAccount.Role.INTERNAL : LdapAccount.Role.EXTERNAL;
    }


    public static final boolean isAmAdmin(Configuration cfg, UserInfo userInfo) {
        return cfg.getAdmins().contains(userInfo.getSubject());
    }


    public static final String getLdapGroupName(String groupEmail) {
        return groupEmail.substring(0, groupEmail.indexOf('@'));
    }


    public static final AccountInfo fromLdap(LdapAccount ldapAccount) {
        AccountInfo info = null;
        if (ldapAccount != null) {
            info = new AccountInfo();
            info.setUsername(ldapAccount.getUsername());
            info.setEmails(ldapAccount.getEmails());
            info.setGivenName(ldapAccount.getGivenName());
            info.setFamilyName(ldapAccount.getFamilyName());
            info.setSubject(ldapAccount.getSubject());
            info.setName(ldapAccount.getName());
            info.setRole(Role.valueOf(ldapAccount.getRole().toString()));
        }
        return info;
    }


    public final static LdapAccount toLdapAccount(GSuiteUser user) {
        LdapAccount account = new LdapAccount();
        account.setSubject(user.getId());
        account.setGivenName(user.getName().getGivenName());
        account.setFamilyName(user.getName().getFamilyName());
        account.setName(user.getName().getFullName());
        account.setRole(LdapAccount.Role.INTERNAL);
        Set<String> emails = new HashSet<>();
        emails.add(user.getPrimaryEmail());
        account.setEmails(emails);
        return account;
    }


    public final static LdapAccount toLdapAccount(String gsuiteDomain, UserInfo userInfo, Set<String> emails,
        CreateAccountData createData) {
        LdapAccount account = new LdapAccount();
        account.setSubject(userInfo.getSubject());
        account.setGivenName(userInfo.getGivenName());
        account.setFamilyName(userInfo.getFamilyName());
        account.setName(userInfo.getName());
        account.setPassword(createData.getPassword());
        account.setUsername(createData.getEmail());
        account.setRole(getLdapRole(userInfo, gsuiteDomain));
        account.setEmails(emails);
        return account;
    }


    public final static LdapAccount toLdapAccount(String gsuiteDomain, UserInfo userInfo, Set<String> emails,
        UpdateAccountData updateData) {
        LdapAccount account = new LdapAccount();
        account.setSubject(userInfo.getSubject());
        account.setGivenName(userInfo.getGivenName());
        account.setFamilyName(userInfo.getFamilyName());
        account.setName(userInfo.getName());
        account.setPassword(updateData.getPassword());
        account.setRole(getLdapRole(userInfo, gsuiteDomain));
        account.setEmails(emails);
        return account;
    }


    public static final CreatedUser toCreated(LdapAccount ldapAccount) {
        return new CreatedUser(
            ldapAccount.getGivenName(),
            ldapAccount.getFamilyName(),
            ldapAccount.getName(),
            ldapAccount.getUsername(),
            ldapAccount.getSubject(),
            ldapAccount.getEmails(),
            eu.hlavki.identity.plugin.api.model.Role.valueOf(ldapAccount.getRole().toString()));
    }
}

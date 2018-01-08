package eu.hlavki.identity.services.sync.impl;

import eu.hlavki.identity.services.google.model.GSuiteUser;
import eu.hlavki.identity.services.ldap.model.LdapAccount;
import java.util.HashSet;
import java.util.Set;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public final class AccountUtil {

    private AccountUtil() {
    }


    public static final boolean isInternalAccount(UserInfo info, String gsuiteDomain) {
        return gsuiteDomain.equals(info.getProperty("hd"));
    }


    public static final LdapAccount.Role getLdapRole(UserInfo userInfo, String gsuiteDomain) {
        return isInternalAccount(userInfo, gsuiteDomain) ? LdapAccount.Role.INTERNAL : LdapAccount.Role.EXTERNAL;
    }


    public static final String getLdapGroupName(String groupEmail) {
        return groupEmail.substring(0, groupEmail.indexOf('@'));
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
}

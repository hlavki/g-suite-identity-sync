package eu.hlavki.identity.services.ldap;

import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import eu.hlavki.identity.services.model.AccountInfo;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LdapAccountService {

    boolean accountExists(String subject) throws LDAPException;


    AccountInfo getAccountInfo(String subject) throws LDAPException;


    List<AccountInfo> getAllAccounts() throws LDAPException;


    void createAccount(LdapAccount account) throws LDAPException;


    void updateAccount(LdapAccount account) throws LDAPException;


    LdapGroup getGroup(String groupName) throws LDAPException;


    Set<String> getAllGroupNames() throws LDAPException;


    Map<String, LdapGroup> getAccountGroups(String accountDN) throws LDAPException;


    String getAccountDN(String subject) throws LDAPException;


    void addGroupMember(String accountDN, String groupName) throws LDAPException;


    void removeGroupMember(String accountDN, String groupName) throws LDAPException;


    void removeGroup(String name) throws LDAPException;


    LdapGroup createOrUpdateGroup(LdapGroup group) throws LDAPException;
}

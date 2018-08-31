package eu.hlavki.identity.services.ldap;

import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface LdapAccountService {

    boolean accountExists(String subject) throws LdapSystemException;


    LdapAccount getAccountInfo(String subject) throws LdapSystemException;


    List<LdapAccount> getAllAccounts() throws LdapSystemException;


    void createAccount(LdapAccount account) throws LdapSystemException;


    void updateAccount(LdapAccount account) throws LdapSystemException;


    LdapGroup getGroup(String groupName) throws LdapSystemException;


    Set<String> getAllGroupNames() throws LdapSystemException;


    Map<String, LdapGroup> getAccountGroups(String accountDN) throws LdapSystemException;


    String getAccountDN(String subject) throws LdapSystemException;


    String getAccountDN(LdapAccount account);


    void addGroupMember(String accountDN, String groupName) throws LdapSystemException;


    void removeGroupMember(String accountDN, String groupName) throws LdapSystemException;


    void removeGroup(String name) throws LdapSystemException;


    LdapGroup createOrUpdateGroup(LdapGroup group) throws LdapSystemException;
}

package eu.hlavki.identity.services.ldap;

import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface LdapAccountService {

    boolean accountExists(String subject) throws LdapSystemException;


    Optional<LdapAccount> searchBySubject(String subject) throws LdapSystemException;


    Optional<LdapAccount> searchByEmail(String email) throws LdapSystemException;


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

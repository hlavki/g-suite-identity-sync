package eu.hlavki.identity.services.ldap;

import eu.hlavki.identity.services.ldap.model.LdapAccount;
import eu.hlavki.identity.services.ldap.model.LdapAccount.Role;
import eu.hlavki.identity.services.ldap.model.LdapGroup;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface LdapAccountService {

    boolean accountExists(String subject);


    Optional<LdapAccount> getAccountBySubject(String subject);


    Optional<LdapAccount> getAccountByEmail(String email);


    List<LdapAccount> searchAccounts(Role role);


    List<LdapAccount> getAllAccounts();


    void createAccount(LdapAccount account);


    void updateAccount(LdapAccount account);


    LdapGroup getGroup(String groupName);


    Set<String> getAllGroupNames();


    List<LdapGroup> getAccountGroups(String accountDN);


    String getAccountDN(String subject);


    String getAccountDN(LdapAccount account);


    void addGroupMember(String accountDN, String groupName);


    void deleteGroupMember(String accountDN, String groupName);


    void deleteGroup(String name);


    LdapGroup createOrUpdateGroup(LdapGroup group);


    void deleteUser(LdapAccount account);


    void deleteUserByEmail(String email);
}

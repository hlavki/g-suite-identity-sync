package camp.xit.identity.services.ldap;

import camp.xit.identity.services.ldap.model.LdapGroup;
import camp.xit.identity.services.model.AccountInfo;
import camp.xit.identity.services.model.CreateAccountData;
import camp.xit.identity.services.model.PrepareAccountData.EmailAddress;
import camp.xit.identity.services.model.UpdateAccountData;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public interface LdapAccountService {

    boolean accountExists(String subject) throws LDAPException;


    AccountInfo getAccountInfo(String subject) throws LDAPException;


    List<AccountInfo> getAllAccounts() throws LDAPException;


    void createAccount(UserInfo userInfo, List<EmailAddress> emails, CreateAccountData createData) throws LDAPException;


    void updateAccount(UserInfo userInfo, UpdateAccountData createData) throws LDAPException;


    LdapGroup getGroup(String groupName) throws LDAPException;


    Set<String> getAllGroupNames() throws LDAPException;


    Map<String, LdapGroup> getAccountGroups(String accountDN) throws LDAPException;


    String getAccountDN(String subject) throws LDAPException;


    void addGroupMember(String accountDN, String groupName) throws LDAPException;


    void removeGroupMember(String accountDN, String groupName) throws LDAPException;


    void removeGroup(String name) throws LDAPException;


    LdapGroup createOrUpdateGroup(LdapGroup group) throws LDAPException;
}

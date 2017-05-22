package camp.xit.identity.services.ldap;

import camp.xit.identity.services.ldap.model.LdapGroup;
import camp.xit.identity.services.model.AccountInfo;
import camp.xit.identity.services.model.CreateAccountData;
import com.unboundid.ldap.sdk.LDAPException;
import java.util.Map;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public interface LdapAccountService {

    boolean accountExists(String subject) throws LDAPException;


    AccountInfo getAccountInfo(String subject) throws LDAPException;


    void createAccount(UserInfo userInfo, CreateAccountData createData) throws LDAPException;


    void updateAccount(UserInfo userInfo, CreateAccountData createData) throws LDAPException;


    Map<String, LdapGroup> getAllLdapGroups() throws LDAPException;


    String getAccountDN(String subject) throws LDAPException;


    void addGroupMember(String accountDN, String groupDN) throws LDAPException;


    void removeGroupMember(String accountDN, String groupDN) throws LDAPException;
}

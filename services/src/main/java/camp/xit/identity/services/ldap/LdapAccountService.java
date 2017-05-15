package camp.xit.identity.services.ldap;

import camp.xit.identity.services.model.AccountInfo;
import camp.xit.identity.services.model.CreateAccountData;
import com.unboundid.ldap.sdk.LDAPException;
import org.apache.cxf.rs.security.oidc.common.UserInfo;
import org.osgi.service.event.EventHandler;

public interface LdapAccountService extends EventHandler {

    boolean accountExists(String subject) throws LDAPException;


    AccountInfo getAccountInfo(String subject) throws LDAPException;


    void createAccount(UserInfo userInfo, CreateAccountData createData) throws LDAPException;


    void updateAccount(UserInfo userInfo, CreateAccountData createData) throws LDAPException;
}

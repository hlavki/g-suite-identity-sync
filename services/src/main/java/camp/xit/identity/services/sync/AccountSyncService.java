package camp.xit.identity.services.sync;

import com.unboundid.ldap.sdk.LDAPException;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public interface AccountSyncService {

    void synchronizeUserGroups(UserInfo userInfo) throws LDAPException;


    void synchronizeAllGroups() throws LDAPException;
}

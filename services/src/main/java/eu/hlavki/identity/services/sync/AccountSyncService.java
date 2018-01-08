package eu.hlavki.identity.services.sync;

import eu.hlavki.identity.services.ldap.LdapSystemException;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public interface AccountSyncService {

    void synchronizeUserGroups(UserInfo userInfo) throws LdapSystemException;


    void synchronizeAllGroups() throws LdapSystemException;


    void synchronizeGSuiteUsers() throws LdapSystemException;
}

package eu.hlavki.identity.services.sync;

import eu.hlavki.identity.services.google.ResourceNotFoundException;
import eu.hlavki.identity.services.ldap.LdapSystemException;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public interface AccountSyncService {

    void synchronizeUserGroups(UserInfo userInfo) throws LdapSystemException;


    void synchronizeGroup(String groupEmail) throws LdapSystemException, ResourceNotFoundException;


    void removeGroup(String groupEmail) throws LdapSystemException;


    void synchronizeAllGroups() throws LdapSystemException;


    void synchronizeGSuiteUsers() throws LdapSystemException;


    void removeUserByEmail(String email) throws LdapSystemException;
}

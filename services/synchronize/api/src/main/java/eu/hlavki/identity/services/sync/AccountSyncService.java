package eu.hlavki.identity.services.sync;

import eu.hlavki.identity.services.google.ResourceNotFoundException;
import org.apache.cxf.rs.security.oidc.common.UserInfo;

public interface AccountSyncService {

    void synchronizeUserGroups(UserInfo userInfo);


    void synchronizeGroup(String groupEmail) throws ResourceNotFoundException;


    void removeGroup(String groupEmail);


    void synchronizeAllGroups();


    void synchronizeGSuiteUsers();


    void removeUserByEmail(String email);


    void cleanExternalUsers();
}

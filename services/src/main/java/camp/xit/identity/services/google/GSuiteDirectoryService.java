package camp.xit.identity.services.google;

import camp.xit.identity.services.google.model.*;
import java.util.Map;

public interface GSuiteDirectoryService {

    /**
     * Retrieve all groups for a member.
     *
     * @param userKey The userKey can be the user's primary email address, the user's alias email address, a
     * group's primary email address, a group's email alias, or the user's unique id.
     * @return all groups for a member
     */
    GroupList getGroups(String userKey);


    /**
     * Read basic group information. This result does not contain group members. Call
     * {@link #getGroupMembers(java.lang.String)} to obtain members.
     *
     * @param groupKey group identifier (email or ID)
     * @return group info
     */
    GSuiteGroup getGroup(String groupKey) throws ResourceNotFoundException;


    /**
     * Read all members for group.
     *
     * @param groupKey group identifier (email or ID)
     * @return group info and members
     */
    GroupMembership getGroupMembers(String groupKey) throws ResourceNotFoundException;


    /**
     * Read all GSuite groups. This result does not contain group members. Call
     * {@link #getGroupMembers(java.lang.String)} to obtain members.
     *
     * @return all GSuite groups
     */
    GroupList getAllGroups();


    /**
     * Read members for all GSuite groups. Because it is very expesive operation, you can use cached values.
     * Cache expires in 15 minutes. To use cache set useCache parameter to true.
     *
     *
     * @param useCache use cached values, if exists.
     * @return members for all GSuite groups. Key is group info object and value is membership
     */
    Map<GSuiteGroup, GroupMembership> getAllGroupMembership(boolean useCache);


    /**
     * Retrieves a gsuite user from Directory API
     *
     * @param userKey Identifies the user in the API request. The value can be the user's primary email
     * address, alias email address, or unique user ID.
     * @return gsuite user
     */
    GSuiteUser getUser(String userKey);


    /**
     * Retrieves list of users or all users in a domain.
     *
     * @return list of gsuite users
     */
    GSuiteUsers getAllUsers();


    void updateUserPassword(String userKey, String password) throws InvalidPasswordException;
}

package camp.xit.identity.services.google;

import camp.xit.identity.services.google.model.GSuiteGroup;
import camp.xit.identity.services.google.model.GroupList;
import camp.xit.identity.services.google.model.GroupMembership;
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
    GSuiteGroup getGroup(String groupKey);


    /**
     * Read all members for group.
     *
     * @param groupKey group identifier (email or ID)
     * @return group info and members
     */
    GroupMembership getGroupMembers(String groupKey);


    /**
     * Read all GSuite groups. This result does not contain group members. Call
     * {@link #getGroupMembers(java.lang.String)} to obtain members.
     *
     * @return all GSuite groups
     */
    GroupList getAllGroups();


    /**
     * Read members for all GSuite groups. Be aware that this is very expesive operation!
     *
     * @return members for all GSuite groups. Key is group info object and value is membership
     */
    Map<GSuiteGroup, GroupMembership> getAllGroupMembership();
}

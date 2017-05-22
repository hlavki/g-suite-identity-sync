package camp.xit.identity.services.google;

import camp.xit.identity.services.google.model.Group;
import camp.xit.identity.services.google.model.GroupList;
import camp.xit.identity.services.google.model.GroupMembership;

public interface GSuiteDirectoryService {

    GroupList getGroups(String userKey);


    Group getGroup(String groupKey);


    GroupMembership getGroupMembers(String groupKey);


    GroupList getAllGroups();
}

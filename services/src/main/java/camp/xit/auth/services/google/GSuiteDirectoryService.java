package camp.xit.auth.services.google;

import camp.xit.auth.services.google.model.Group;
import camp.xit.auth.services.google.model.GroupList;
import camp.xit.auth.services.google.model.GroupMembership;

public interface GSuiteDirectoryService {

    GroupList getGroups(String userKey);


    Group getGroup(String groupKey);


    GroupMembership getGroupMembers(String groupKey);
}

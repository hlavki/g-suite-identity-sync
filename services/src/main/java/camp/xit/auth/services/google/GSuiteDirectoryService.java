package camp.xit.auth.services.google;

public interface GSuiteDirectoryService {

    GroupMembershipResponse getGroupMembers(String groupKey);
}

package camp.xit.auth.services.rest.directory;

public interface GSuiteDirectoryService {

    GroupMembershipResponse getGroupMembers(String groupKey);
}

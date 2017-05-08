package camp.xit.auth.services.google;

import camp.xit.auth.services.model.GroupMember;
import camp.xit.auth.services.rest.util.StringUtils;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupMembershipResponse {

    private String etag;
    private String kind;
    private String nextPageToken;
    private List<GroupMember> members;


    public GroupMembershipResponse() {
    }


    public String getEtag() {
        return etag;
    }


    public void setEtag(String etag) {
        this.etag = etag;
    }


    public String getKind() {
        return kind;
    }


    public void setKind(String kind) {
        this.kind = kind;
    }


    public String getNextPageToken() {
        return nextPageToken;
    }


    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }


    public List<GroupMember> getMembers() {
        return members;
    }


    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }


    @Override
    public String toString() {
        return StringUtils.objectToString("membership", this);
    }
}

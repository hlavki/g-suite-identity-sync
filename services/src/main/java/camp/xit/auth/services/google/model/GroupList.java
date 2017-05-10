package camp.xit.auth.services.google.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupList {

    private String kind;
    private List<Group> groups;
    private String nextPageToken;


    public GroupList() {
    }


    public GroupList(String kind, List<Group> groups) {
        this.kind = kind;
        this.groups = groups;
    }


    public String getKind() {
        return kind;
    }


    public void setKind(String kind) {
        this.kind = kind;
    }


    public List<Group> getGroups() {
        return groups;
    }


    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }


    public String getNextPageToken() {
        return nextPageToken;
    }


    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

}

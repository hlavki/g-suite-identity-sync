package camp.xit.identity.services.google.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GroupList {

    private String kind;
    private List<GSuiteGroup> groups;
    private String nextPageToken;


    public GroupList() {
    }


    public GroupList(String kind, List<GSuiteGroup> groups) {
        this.kind = kind;
        this.groups = groups;
    }


    public String getKind() {
        return kind;
    }


    public void setKind(String kind) {
        this.kind = kind;
    }


    public List<GSuiteGroup> getGroups() {
        return groups;
    }


    public void setGroups(List<GSuiteGroup> groups) {
        if (groups == null) {
            groups = new ArrayList<>();
        }
        this.groups = groups;
    }


    public String getNextPageToken() {
        return nextPageToken;
    }


    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

}

package camp.xit.identity.services.google.model;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GSuiteUsers {

    private String etag;
    private String kind;
    private String nextPageToken;
    private List<GSuiteUser> users;


    public GSuiteUsers() {
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


    public List<GSuiteUser> getUsers() {
        return users;
    }


    public void setUsers(List<GSuiteUser> users) {
        this.users = users;
    }

}

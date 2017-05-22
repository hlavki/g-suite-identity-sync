package camp.xit.identity.services.ldap.model;

import camp.xit.identity.services.util.StringUtils;
import java.util.Set;

public class LdapGroup {

    private String dn;
    private Set<String> members;


    public LdapGroup() {
    }


    public LdapGroup(String dn, Set<String> members) {
        this.dn = dn;
        this.members = members;
    }


    public String getDn() {
        return dn;
    }


    public void setDn(String dn) {
        this.dn = dn;
    }


    public Set<String> getMembers() {
        return members;
    }


    public void setMembers(Set<String> members) {
        this.members = members;
    }


    @Override
    public String toString() {
        return StringUtils.toStringLine(this);
    }
}

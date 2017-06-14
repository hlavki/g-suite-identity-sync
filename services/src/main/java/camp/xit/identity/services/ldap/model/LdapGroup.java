package camp.xit.identity.services.ldap.model;

import camp.xit.identity.services.util.StringUtils;
import java.util.Set;

public class LdapGroup {

    private String name;
    private String dn;
    private String description;
    private Set<String> membersDn;


    public LdapGroup() {
    }


    public LdapGroup(String name, String dn, String description, Set<String> members) {
        this.name = name;
        this.dn = dn;
        this.membersDn = members;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDn() {
        return dn;
    }


    public void setDn(String dn) {
        this.dn = dn;
    }


    /**
     * Return set of membersDn DN.
     *
     * @return set of membersDn DN.
     */
    public Set<String> getMembersDn() {
        return membersDn;
    }


    public void setMembersDn(Set<String> membersDn) {
        this.membersDn = membersDn;
    }


    @Override
    public String toString() {
        return StringUtils.toStringLine(this);
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }
}

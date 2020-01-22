package eu.hlavki.identity.services.ldap.model;

import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LdapAccount {

    public enum Role {
        EXTERNAL, INTERNAL
    }

    private String dn;
    private String name;
    private String givenName;
    private String familyName;
    private String username;
    private String subject;
    private Set<String> emails;
    private String password;
    private Role role;


    public LdapAccount(String dn) {
        this.dn = dn;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        sb.append("givenName=").append(givenName).
                append(", familyName=").append(familyName).
                append(", name=").append(name).
                append(", username=").append(username).
                append(", subject=").append(subject).
                append(", emails=").append(emails).
                append(", role=").append(role).append("]");
        return sb.toString();
    }

    public String getValueByAttr(String attr) {
        switch (attr) {
            case "cn": return getName();
            case "uid": return getUsername();
            default: return null;
        }
    }
}

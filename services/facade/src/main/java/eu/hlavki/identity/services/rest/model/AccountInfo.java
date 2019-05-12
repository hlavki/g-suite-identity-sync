package eu.hlavki.identity.services.rest.model;

import eu.hlavki.identity.services.ldap.model.LdapAccount;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement
public class AccountInfo {

    private String username;
    private Set<String> emails;
    private String subject;
    private String givenName;
    private String familyName;
    private String name;
    private Role role;
    private boolean syncGsuitePassword;


    public AccountInfo(LdapAccount ldapAccount) {
        this.setUsername(ldapAccount.getUsername());
        this.setEmails(ldapAccount.getEmails());
        this.setGivenName(ldapAccount.getGivenName());
        this.setFamilyName(ldapAccount.getFamilyName());
        this.setSubject(ldapAccount.getSubject());
        this.setName(ldapAccount.getName());
        this.setRole(Role.valueOf(ldapAccount.getRole().toString()));
    }


    public AccountInfo(String username, Set<String> emails, String subject, String givenName,
        String familyName, String name, Role role, boolean syncGsuitePassword) {
        this.username = username;
        this.emails = emails;
        this.subject = subject;
        this.givenName = givenName;
        this.familyName = familyName;
        this.name = name;
        this.role = role;
    }
}

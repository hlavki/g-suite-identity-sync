package eu.hlavki.identity.services.rest.model;

import java.net.URI;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement
public class PrepareAccountData {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Group {

        private String name;
        private String email;


        public static Group map(eu.hlavki.identity.services.google.model.GSuiteGroup group) {
            return new Group(group.getName(), group.getEmail());
        }
    }

    private String givenName;
    private String familyName;
    private String name;
    private Set<String> emails;
    private String email;
    private URI profilePicture;
    private boolean emailVerified;
    private Role role;
    private boolean saveGSuitePassword;
    private List<Group> groups;
}

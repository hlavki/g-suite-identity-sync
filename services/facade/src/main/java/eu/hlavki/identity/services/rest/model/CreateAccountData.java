package eu.hlavki.identity.services.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@XmlRootElement
public class CreateAccountData {

    @NotNull
    private String email;
    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'\"\\+\\-=~`])[0-9a-zA-Z!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'\"\\+\\-=~`]{8,}$", message = "The password does not meet the defined rules. This shouldn't happen!")
    private String password;
    @NotNull
    private String confirmPassword;
    private boolean saveGSuitePassword;
}

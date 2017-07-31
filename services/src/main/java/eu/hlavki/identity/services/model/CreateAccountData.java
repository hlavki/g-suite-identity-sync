package eu.hlavki.identity.services.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateAccountData {

    @NotNull
    private String email;
    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'\"\\+\\-=~`])[0-9a-zA-Z!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'\"\\+\\-=~`]{8,}$", message = "The password does not meet the defined rules. Don't try to fuck with me!")
    private String password;
    @NotNull
    private String confirmPassword;
    private boolean saveGSuitePassword;


    public CreateAccountData() {
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getConfirmPassword() {
        return confirmPassword;
    }


    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }


    public boolean isSaveGSuitePassword() {
        return saveGSuitePassword;
    }


    public void setSaveGSuitePassword(boolean saveGSuitePassword) {
        this.saveGSuitePassword = saveGSuitePassword;
    }
}

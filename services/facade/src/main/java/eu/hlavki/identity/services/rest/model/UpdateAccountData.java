package eu.hlavki.identity.services.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UpdateAccountData {

    @NotNull
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'\"\\+\\-=~`])[0-9a-zA-Z!\\?@#\\$%\\^&\\*_\\.,:;\\[\\]\\(\\){}<>\'\"\\+\\-=~`]{8,}$", message = "The password does not meet the defined rules. This shouldn't happen!")
    private String password;
    @NotNull
    private String confirmPassword;
    private boolean saveGSuitePassword;


    public UpdateAccountData() {
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

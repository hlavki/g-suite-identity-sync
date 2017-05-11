package camp.xit.auth.services.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateAccountData {

    private String email;
    private String password;
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

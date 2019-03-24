package eu.hlavki.identity.services.ldap;

public class LdapSystemException extends RuntimeException {

    private static final long serialVersionUID = 1L;


    public LdapSystemException(String message) {
        super(message);
    }


    public LdapSystemException(String message, Throwable cause) {
        super(message, cause);
    }


    public LdapSystemException(Throwable cause) {
        super(cause);
    }


    public LdapSystemException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

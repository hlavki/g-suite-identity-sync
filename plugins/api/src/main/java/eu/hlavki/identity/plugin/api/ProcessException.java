package eu.hlavki.identity.plugin.api;

public class ProcessException extends Exception {

    private static final long serialVersionUID = 1L;


    public ProcessException() {
    }


    public ProcessException(String message) {
        super(message);
    }


    public ProcessException(String message, Throwable cause) {
        super(message, cause);
    }


    public ProcessException(Throwable cause) {
        super(cause);
    }


    public ProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}

package camp.xit.identity.services.google;

public class InvalidPasswordException extends Exception {

    private static final long serialVersionUID = 2712920621905135028L;

    public InvalidPasswordException() {
        super();
    }


    public InvalidPasswordException(String message) {
        super(message);
    }


    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }


    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }
}

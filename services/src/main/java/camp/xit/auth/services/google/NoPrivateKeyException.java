package camp.xit.auth.services.google;

public class NoPrivateKeyException extends Exception {

    private static final long serialVersionUID = 2712920621905135028L;

    public NoPrivateKeyException() {
        super();
    }


    public NoPrivateKeyException(String message) {
        super(message);
    }


    public NoPrivateKeyException(String message, Throwable cause) {
        super(message, cause);
    }


    public NoPrivateKeyException(Throwable cause) {
        super(cause);
    }
}

package smchan.freemind_my_plugin;

/**
 * Simple exception class encapsulating user cancellation
 */
public class UserCancelledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UserCancelledException() {
        super();
    }

    public UserCancelledException(String message) {
        super(message);
    }

    public UserCancelledException(String message, Throwable cause) {
        super(message, cause);
    }
}

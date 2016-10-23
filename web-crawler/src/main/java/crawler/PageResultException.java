package crawler;

/**
 * Exception class to identify crawler-specific exceptions.
 */
public class PageResultException extends Exception {
    public PageResultException(String message) {
        super(message);
    }

    public PageResultException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public PageResultException(Throwable throwable) {
        super(throwable);
    }
}

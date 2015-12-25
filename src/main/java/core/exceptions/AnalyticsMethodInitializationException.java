package core.exceptions;

/**
 * An exception to be thrown whenever the AnalyticsMethod Initializaiton does not end correctly.
 */
public class AnalyticsMethodInitializationException extends Throwable {
    public AnalyticsMethodInitializationException(String validationMessage) {
        super(validationMessage);
    }
}

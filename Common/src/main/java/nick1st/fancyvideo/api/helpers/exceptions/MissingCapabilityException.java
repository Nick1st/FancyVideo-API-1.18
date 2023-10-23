package nick1st.fancyvideo.api.helpers.exceptions;

/**
 * This exception is thrown when a player is missing a capability required for the called method.
 * @since 3.0.0
 */
public class MissingCapabilityException extends PlayerException {

    public final String missingCapability;

    /**
     * @since 3.0.0
     */
    public MissingCapabilityException(String missingCapability) {
        this.missingCapability = missingCapability;
    }
}

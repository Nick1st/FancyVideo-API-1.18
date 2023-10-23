package nick1st.fancyvideo.api.helpers.exceptions;

/**
 * This exception is thrown when you call a method on a player, but the method is invalid in the context of the current
 * player state.
 * @apiNote Player implementations can subclass this exception if they want to provide more details.
 * @since 3.0.0
 */
public class MethodUnsupportedPlayerStateException extends PlayerException {
    public final String currentPlayerState;
    public final String[] allowedPlayerStates;

    /**
     * @param currentPlayerState The state the player was in as this exception was raised.
     * @param allowedPlayerStates All states that would have been valid for the method called.
     * @since 3.0.0
     */
    public MethodUnsupportedPlayerStateException(String currentPlayerState, String[] allowedPlayerStates) {
        this.currentPlayerState = currentPlayerState;
        this.allowedPlayerStates = allowedPlayerStates;
    }
}

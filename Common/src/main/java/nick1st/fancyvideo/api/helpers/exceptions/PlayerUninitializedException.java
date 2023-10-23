package nick1st.fancyvideo.api.helpers.exceptions;

import nick1st.fancyvideo.api.player.PlayerState;

/**
 * This exception is thrown when you call a method on a player with state {@link PlayerState#UNINITIALIZED}, but a
 * different state is required.
 * @apiNote Player implementations can subclass this exception if they want to provide more details.
 * @since 3.0.0
 */
public class PlayerUninitializedException extends MethodUnsupportedPlayerStateException {

    /**
     * @param allowedPlayerStates All states that would have been valid for the method called.
     * @since 3.0.0
     */
    public PlayerUninitializedException(String[] allowedPlayerStates) {
        super(PlayerState.UNINITIALIZED.asString(), allowedPlayerStates);
    }
}

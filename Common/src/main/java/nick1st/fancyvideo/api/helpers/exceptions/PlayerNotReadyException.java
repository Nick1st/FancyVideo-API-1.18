package nick1st.fancyvideo.api.helpers.exceptions;

import nick1st.fancyvideo.api.player.PlayerState;

/**
 * This exception is thrown when you call a method on a player and state is {@link PlayerState#INITIALIZED} or
 * {@link PlayerState#STOPPED}, but {@link PlayerState#READY} is needed.
 * @apiNote Player implementations can subclass this exception if they want to provide more details.
 * @since 3.0.0
 */
public class PlayerNotReadyException extends  MethodUnsupportedPlayerStateException {

    /**
     * @param currentPlayerState The state the player was in.
     * @since 3.0.0
     */
    public PlayerNotReadyException(String currentPlayerState) {
        super(currentPlayerState, new String[]{PlayerState.READY.asString()});
    }
}

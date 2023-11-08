package nick1st.fancyvideo.api.helpers.exceptions.player;

import nick1st.fancyvideo.api.player.PlayerState;

/**
 * This player has been disposed, but a method on it was called.
 * @since 3.0.0
 */
public class PlayerDisposedException extends MethodUnsupportedPlayerStateException {

    /**
     * @since 3.0.0
     */
    public PlayerDisposedException() {
        super(PlayerState.DISPOSED.asString(), new String[0]);
    }
}

package nick1st.fancyvideo.api.player;

/**
 * Specifies the state a media player is in. Some methods on a player should behave different depending on the state the
 * player is in.
 * @since 3.0.0
 */
public enum PlayerState {
    /**
     * The player is missing crucial information to finish initialisation.<br>(This normally means native resources have
     * not been allocated to this player.) <br><br>
     * Next valid states: <br>
     * - {@link #INITIALIZED} Default next state<br>
     * - {@link #INVALID} The player is in an invalid, <b>UNRECOVERABLE</b> state.<br>
     * - {@link #DISPOSED} The player is no longer needed and should run its cleanup phase.
     * @since 3.0.0
     */
    UNINITIALIZED,

    /**
     * The player is having all crucial information and finished initialisation.<br>(This normally means native
     * resources have been allocated to this player.) <br><br>
     * Next valid states: <br>
     * - {@link #READY} Default next state<br>
     * - {@link #INVALID} The player is in an invalid, <b>UNRECOVERABLE</b> state.<br>
     * - {@link #DISPOSED} The player is no longer needed and should run its cleanup phase.
     * @since 3.0.0
     */
    INITIALIZED,

    /**
     * The player can start playback if requested to do so. <br><br>
     * Next valid states: <br>
     * - {@link #PLAYING} Default next state<br>
     * - {@link #INVALID} The player is in an invalid, <b>UNRECOVERABLE</b> state.<br>
     * - {@link #DISPOSED} The player is no longer needed and should run its cleanup phase.
     * @since 3.0.0
     */
    READY,

    /**
     * The player started playback and playback is currently running. <br><br>
     * Next valid states: <br>
     * - {@link #PAUSED} Possible next state. <br>
     * - {@link #STOPPED} Possible next state. <br>
     * - {@link #INVALID}  If an error occurs during playback, the player should only get into the invalid state, if the
     * error is unrecoverable. If it is recoverable, the player should go to the {@link #STOPPED} state instead.<br>
     * - {@link #DISPOSED} The player is no longer needed and should run its cleanup phase.
     * @since 3.0.0
     */
    PLAYING,

    /**
     * The player is currently paused. <br><br>
     * Next valid states: <br>
     * - {@link #PLAYING} Possible next state. <br>
     * - {@link #STOPPED} Further playback is canceled. <br>
     * - {@link #INVALID} The player is in an invalid, <b>UNRECOVERABLE</b> state.<br>
     * - {@link #DISPOSED} The player is no longer needed and should run its cleanup phase.
     * @since 3.0.0
     */
    PAUSED,

    /**
     * The player is currently paused. <br><br>
     * Next valid states: <br>
     * - {@link #READY} Possible next state. This state transition should be caused by resetting certain aspects of the
     * player, allowing it to start playback of a (new) media element.<br>
     * - {@link #INVALID} The player is in an invalid, <b>UNRECOVERABLE</b> state.<br>
     * - {@link #DISPOSED} The player is no longer needed and should run its cleanup phase.
     * @since 3.0.0
     */
    STOPPED,

    /**
     * The player is in an invalid, <b>UNRECOVERABLE</b> state.
     * Next valid states: <br>
     * - {@link #DISPOSED} The player should run its cleanup phase after the invalid state was acknowledged by the
     * implementation.
     * @since 3.0.0
     */
    INVALID,

    /**
     * The player must release <b>ALL</b> (native) resources. All references to this player should be discarded so GC
     * can clean it up.
     * @since 3.0.0
     */
    DISPOSED;

    public String asString() {
        return this.getClass().getSimpleName() + name();
    }
}

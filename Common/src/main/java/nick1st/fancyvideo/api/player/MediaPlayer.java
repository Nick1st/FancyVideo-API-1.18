package nick1st.fancyvideo.api.player;

import nick1st.fancyvideo.api.helpers.MediaSource;
import nick1st.fancyvideo.api.helpers.annotations.CalledByQuery;
import nick1st.fancyvideo.api.helpers.capabilities.DefaultCapabilities;
import nick1st.fancyvideo.api.helpers.capabilities.RequiresCapability;
import nick1st.fancyvideo.api.helpers.exceptions.player.*;

import java.io.Closeable;

/**
 * This abstract base class provides a reasonable amount of methods <b>ALL</b> types of media players should support.
 * Methods annotated with @{@link RequiresCapability} are optional, as some media types (and such their players) might
 * not be able to support them.
 *
 * @since 3.0.0
 */
public abstract class MediaPlayer implements Closeable {

    /**
     * The current player state
     * @apiNote It is up to the implementation to keep this field up to date.
     * @since 3.0.0
     */
    protected PlayerState state = PlayerState.UNINITIALIZED;

    /**
     * Metadata field used for creation and query of this player
     * @since 3.0.0
     */
    protected PlayerFactoryHelper factoryHelper;

    /**
     * This ctor is used during a MediaPlayer search query. If that is the case (param bool isQuery is true),
     * it should <b>NEVER</b> allocate or create native resources,
     * unless that is needed to check if this player instance can function at all. {@link #close()} is always called at
     * the end of the query.
     * @param factoryHelper a metadata object used to create this player
     * @param isQuery if this invocation is caused by a query
     * @apiNote <b>THIS CTOR MUST BE IMPLEMENTED!</b>
     * @since 3.0.0
     */
    @CalledByQuery
    public MediaPlayer(PlayerFactoryHelper factoryHelper, boolean isQuery) {
        if (isQuery) {
            this.factoryHelper = factoryHelper;
        }
    }

    /**
     * Sets the media source this player should play from.
     * @param source the mediaSource this player should play from
     * @since 3.0.0
     */
    @CalledByQuery
    public abstract void setMediaSource(MediaSource source);

    /**
     * Starts playback if player is currently in state {@link PlayerState#READY} <br>
     * When and <b>ONLY WHEN</b> calling this method with strict=false: <br>
     * Resumes playback if player is currently in state {@link PlayerState#PAUSED} <br>
     * Silently ignores if player is currently in state {@link PlayerState#PLAYING}
     * <br><br>
     * You probably don't want to override this method, see {@link #_play()} instead.
     * @param strict If true, this won't resume a paused player, or silently accept that the player is already playing.
     * @throws MethodUnsupportedPlayerStateException or a subclass depending on various conditions
     */
    public void play(boolean strict) {
        switch (getState()) {
            case UNINITIALIZED -> throw new PlayerUninitializedException(strict ?
                    new String[]{PlayerState.READY.asString()} :
                    new String[]{PlayerState.READY.asString(), PlayerState.PAUSED.asString()
                            , PlayerState.PLAYING.asString()});
            case INITIALIZED, STOPPED -> throw new PlayerNotReadyException(getState().asString());
            case PLAYING, PAUSED -> {
                if (strict) {
                    throw new MethodUnsupportedPlayerStateException(getState().asString(),
                            new String[]{PlayerState.READY.asString()});
                }
                if (getState() == PlayerState.PAUSED) {
                    resume();
                }
            }
            case READY -> _play();
            case INVALID -> checkState(null);
            case DISPOSED -> throw new PlayerDisposedException();
        }
    }

    /**
     * Starts playback if player is currently in state {@link PlayerState#READY} <br>
     * Resumes playback if player is currently in state {@link PlayerState#PAUSED} <br>
     * @apiNote This method is a shortcut for calling {@link #play(boolean)} with false.
     * @since 3.0.0
     */
    public void play() {
        play(false);
    }

    /**
     * Internal method for starting playback. This is only initial start, <b>NOT</b> fired on resume.
     * @see #play(boolean)
     * @since 3.0.0
     */
    protected abstract void _play();

    /**
     * Pauses playback
     * @since 3.0.0
     * @throws MissingCapabilityException if the player does not have the capability required to invoke this method.
     * @throws MethodUnsupportedPlayerStateException if the player was not in state {@link PlayerState#PLAYING}
     */
    @RequiresCapability(DefaultCapabilities.PAUSE)
    public void pause() {
        checkCapability(DefaultCapabilities.PAUSE);
        checkState(PlayerState.PLAYING);
    }

    /**
     * Resumes playback
     * @since 3.0.0
     * @throws MissingCapabilityException if the player does not have the capability required to invoke this method.
     * @throws MethodUnsupportedPlayerStateException if the player was not in state {@link PlayerState#PAUSED}
     */
    @RequiresCapability(DefaultCapabilities.PAUSE)
    public void resume(){
        checkCapability(DefaultCapabilities.PAUSE);
        checkState(PlayerState.PAUSED);
    }

    /**
     * Stops playback.
     * @since 3.0.0
     */
    public abstract void stop();

    /**
     * @param capability The capability this implementation might have / might not have
     * @return true if the capability is there, false otherwise
     * @apiNote The result of this method for a specific input can change during the players' lifetime, however
     * <b>ONLY</b> from <b>false to true</b>.
     * @since 3.0.0
     */
    @CalledByQuery
    public abstract boolean hasCapability(String capability);

    /**
     * Checks if this player has a capability
     * @param capability The capability to check for
     * @throws MissingCapabilityException if the capability is not present on this player
     * @since 3.0.0
     */
    public final void checkCapability(String capability) {
        if (!hasCapability(capability))
            throw new MissingCapabilityException(capability);
    }

    /**
     * Checks if this player is in a specific state
     * @param state The state to check if the player is in
     * @throws MethodUnsupportedPlayerStateException if the state of the player is not the state checked for
     * @since 3.0.0
     */
    public final void checkState(PlayerState state) {
        if (state == null || this.getState() == PlayerState.INVALID) {
            onInvalid();
            throw new MethodUnsupportedPlayerStateException(PlayerState.INVALID.asString(), new String[]{});
        }
        if (this.getState() != state)
            throw new MethodUnsupportedPlayerStateException(this.getState().asString(), new String[]{state.asString()});
    }

    /**
     * Called if player is in an invalid, non-recoverable state. <br>
     * @apiNote Implementations should override this method.
     * @since 3.0.0
     */
    public void onInvalid() {
        close();
    }

    /**
     * Releases all resources held by this player.
     * Use {@link #factoryHelper} to check if this was only called because of a query ending, and act accordingly.
     * @since 3.0.0
     */
    @CalledByQuery
    @Override
    public abstract void close();

    /**
     * The current player state
     * @apiNote It is up to the implementation to keep this field up to date.
     * @since 3.0.0
     */
    public PlayerState getState() {
        return state;
    }
}

package de.nick1st.fancyvideo.backends.gst;

import nick1st.fancyvideo.api.helpers.exceptions.player.MethodUnsupportedPlayerStateException;
import nick1st.fancyvideo.api.helpers.exceptions.player.MissingCapabilityException;
import nick1st.fancyvideo.api.helpers.formats.MediaFormat;
import nick1st.fancyvideo.api.player.MediaPlayer;
import nick1st.fancyvideo.api.player.PlayerFactoryHelper;
import nick1st.fancyvideo.api.player.PlayerState;
import nick1st.fancyvideo.api.player.PlayerSupportsVideo;
import org.freedesktop.gstreamer.elements.PlayBin;

public class GstVideoPlayer extends MediaPlayer implements PlayerSupportsVideo { // TODO Work on this next time opened NO STATICS!!!

    private final PlayerFactoryHelper initInfo;
    private PlayBin playBin;

    public GstVideoPlayer(PlayerFactoryHelper initInfo) {
        this.initInfo = initInfo;
        playBin = new PlayBin("MainPlayBinContainer::" + initInfo.getPlayerResourceLocation().toString());
    }

    @Override
    protected void _play() {

    }

    /**
     * Pauses playback
     *
     * @throws MissingCapabilityException            if the player does not have the capability required to invoke this method.
     * @throws MethodUnsupportedPlayerStateException if the player was not in state {@link PlayerState#PLAYING}
     * @since 3.0.0
     */
    @Override
    public void pause() {
        super.pause();
    }

    /**
     * Resumes playback
     *
     * @throws MissingCapabilityException            if the player does not have the capability required to invoke this method.
     * @throws MethodUnsupportedPlayerStateException if the player was not in state {@link PlayerState#PLAYING}
     * @since 3.0.0
     */
    @Override
    public void resume() {
        super.resume();
    }

    /**
     * Stops playback.
     *
     * @since 3.0.0
     */
    @Override
    public void stop() {

    }

    /**
     * @param capability The capability this implementation might have / might not have
     * @return true if the capability is there, false otherwise
     * @apiNote The result of this method for a specific input can change during the players' lifetime, however
     * <b>ONLY</b> from <b>false to true</b>.
     * @since 3.0.0
     */
    @Override
    public boolean hasCapability(String capability) {
        return false;
    }

    /**
     * Called if player is in an invalid, non-recoverable state. <br>
     *
     * @apiNote Implementations should override this method.
     * @since 3.0.0
     */
    @Override
    public void onInvalid() {
        super.onInvalid();
    }

    /**
     * Releases all resources held by this player.
     *
     * @since 3.0.0
     */
    @Override
    public void close() {

    }

    /**
     * @return an array of sink {@link MediaFormat}s (the output types) supported by this player.
     * @since 3.0.0
     */
    @Override
    public MediaFormat[] getSupportedVideoSinkFormats() {
        return new MediaFormat[0];
    }

    /**
     * Sets the format the player should sink as. Should be called in {@link PlayerState#INITIALIZED}
     *
     * @param sinkFormat The (supported) format this player will sink as
     * @apiNote Remember that the format set here is a <b>BINDING</b> contract
     * @since 3.0.0
     */
    @Override
    public void setVideoSinkFormat(MediaFormat sinkFormat) {

    }
}

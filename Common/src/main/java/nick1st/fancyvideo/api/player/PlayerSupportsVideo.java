package nick1st.fancyvideo.api.player;

import nick1st.fancyvideo.api.helpers.formats.MediaFormat;

/**
 * This interface provides typical method existing on a player supporting video.
 * @since 3.0.0
 */
public interface PlayerSupportsVideo {

    /**
     * @return an array of sink {@link MediaFormat}s (the output types) supported by this player.
     * @since 3.0.0
     */
    MediaFormat[] getSupportedVideoSinkFormats();

    /**
     * Sets the format the player should sink as. Should be called in {@link PlayerState#INITIALIZED}
     * @param sinkFormat The (supported) format this player will sink as
     * @apiNote Remember that the format set here is a <b>BINDING</b> contract
     * @since 3.0.0
     */
    void setVideoSinkFormat(MediaFormat sinkFormat);
}

package nick1st.fancyvideo.api.player;

import nick1st.fancyvideo.api.helpers.annotations.CalledByQuery;
import nick1st.fancyvideo.api.helpers.formats.VideoFormatNew;

/**
 * This interface provides typical method existing on a player supporting video.
 * @since 3.0.0
 */
public interface PlayerSupportsVideo {

    /**
     * @param videoFormat the video format requested
     * @return true of the player can provide this format, false otherwise
     * @since 3.0.0
     */
    @CalledByQuery
    boolean supportsVideoSinkFormat(VideoFormatNew<?> videoFormat);

    /**
     * Sets the format the player should sink as. Should be called in {@link PlayerState#INITIALIZED}
     * @param sinkFormat The (supported) format this player will sink as
     * @apiNote Remember that the format set here is a <b>BINDING</b> contract
     * @since 3.0.0
     */
    void setVideoSinkFormat(VideoFormatNew<?> sinkFormat);
}

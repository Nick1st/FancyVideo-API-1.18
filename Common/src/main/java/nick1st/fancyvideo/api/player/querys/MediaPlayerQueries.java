package nick1st.fancyvideo.api.player.querys;

import nick1st.fancyvideo.api.player.MediaPlayer;
import nick1st.fancyvideo.api.player.PlayerSupportsAudio;
import nick1st.fancyvideo.api.player.PlayerSupportsVideo;

/**
 * A set of common queries that every subclass of {@link MediaPlayer} needs to support.
 * @since 3.0.0
 */
public class MediaPlayerQueries {

    /**
     * Checks if the MediaPlayer has/is a specific super class. Can also be used if the player implements a specific
     * interface.
     * @param type The class of the type the player should be assignable from
     * @return A function that evaluates the expression
     * @since 3.0.0
     */
    public static Query IsInstanceOf(Class<?> type) {
        return mediaPlayer -> type.isAssignableFrom(mediaPlayer.getClass());
    }

    /**
     * Checks if the MediaPlayer supports video.
     * @return A function that evaluates the expression
     * @since 3.0.0
     */
    public static Query SupportsVideo() {
        return mediaPlayer -> PlayerSupportsVideo.class.isAssignableFrom(mediaPlayer.getClass());
    }

    /**
     * Checks if the MediaPlayer supports audio.
     * @return A function that evaluates the expression
     * @since 3.0.0
     */
    public static Query SupportsAudio() {
        return mediaPlayer -> PlayerSupportsAudio.class.isAssignableFrom(mediaPlayer.getClass());
    }

    /**
     * Checks if the MediaPlayer has a specific capability.
     * @return A function that evaluates the expression
     * @since 3.0.0
     */
    public static Query HasCapability(String capability) {
        return mediaPlayer -> mediaPlayer.hasCapability(capability);
    }
}


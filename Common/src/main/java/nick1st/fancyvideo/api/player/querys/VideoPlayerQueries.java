package nick1st.fancyvideo.api.player.querys;

import nick1st.fancyvideo.api.helpers.formats.VideoFormatNew;
import nick1st.fancyvideo.api.player.PlayerSupportsVideo;

public class VideoPlayerQueries {

    /**
     * VideoQuery methods will try top cast the Player to a PlayerSupports Video.
     */
    @FunctionalInterface
    public interface VideoQuery extends Query {} // TODO Typesafe this stuff

    /**
     * Checks if the MediaPlayer supports video.
     * @return A function that evaluates the expression
     * @since 3.0.0
     */
    public static VideoQuery SupportsVideo() {
        return mediaPlayer -> PlayerSupportsVideo.class.isAssignableFrom(mediaPlayer.getClass());
    }

    public static VideoQuery SupportsOutputFormat(VideoFormatNew<?> mediaFormat) {
        return mediaPlayer -> {
            if (mediaPlayer instanceof PlayerSupportsVideo videoPlayer) {
                return videoPlayer.supportsVideoSinkFormat(mediaFormat);
            } else {
                throw new RuntimeException("Query not a video query");
            }
        };
    }
}

package nick1st.fancyvideo.api.helpers;

/**
 * This interface describes the type of media used in the application. <br>
 * Currently known types: <br>
 * - {@link #AUDIO} <br>
 * - {@link #VIDEO} <br>
 * - {@link #SUBTITLES} <br>
 *
 * If you think there's something missing here, feel free to discuss it with me on my Discord.
 * @since 3.0.0
 */
public enum MediaType {
    /**
     * Media (or part of a media) that goes to an audio filter or sink.
     * @since 3.0.0
     */
    AUDIO,

    /**
     * Media (or part of a media) that goes to a video filter or sink.
     * @since 3.0.0
     */
    VIDEO,

    /**
     * Media (or part of a media) that goes to a subtitle filter or sink.
     * @since 3.0.0
     */
    SUBTITLES
}

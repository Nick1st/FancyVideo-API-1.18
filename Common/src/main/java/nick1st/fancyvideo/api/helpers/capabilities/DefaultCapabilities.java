package nick1st.fancyvideo.api.helpers.capabilities;

/**
 * This class contains a set of the most basic/important capabilities.
 * @since 3.0.0
 */
public final class DefaultCapabilities {

    private DefaultCapabilities() {}

    /**
     * This capability indicates the media player supports pausing.
     * @apiNote This does <b>NOT</b> indicate the player will pause immediately.
     * @since 3.0.0
     */
    public static final String PAUSE = "caps.default.pause";

    /**
     * This capability indicates the media player supports pausing on a specific frame.
     * @since 3.0.0
     */
    public static final String PERFECT_PAUSE = "caps.default.perfect_pause";

    /**
     * This capability indicates the player supports setting a volume on the audio track between 0.0f and 1.0f.
     * @since 3.0.0
     */
    public static final String VOLUME = "caps.default.volume";

    /**
     * This capability indicates the player supports boosting the volume on the audio track up to 2.0f.
     * @apiNote This capability does not include the {@link #VOLUME} capability.
     * @since 3.0.0
     */
    public static final String BOOST_VOLUME = "caps.default.boost_volume";

    /**
     * This capability indicates the player supports boosting the volume on the audio track over 2.0f.
     * @apiNote This capability includes the {@link #BOOST_VOLUME}, but not the {@link #VOLUME} capability.
     * @since 3.0.0
     */
    public static final String BOOST_VOLUME_PLUS = "caps.default.boost_volume_plus";

    /**
     * This capability indicates the player supports converting directional audio sources to mono sources.
     * @apiNote This capability does at least require stereo to mono conversion, other conversions are optional.
     * @since 3.0.0
     */
    public static final String MONO_CONVERT = "caps.default.mono_convert";
}

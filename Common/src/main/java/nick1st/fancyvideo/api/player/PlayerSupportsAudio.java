package nick1st.fancyvideo.api.player;

import nick1st.fancyvideo.api.helpers.capabilities.DefaultCapabilities;
import nick1st.fancyvideo.api.helpers.capabilities.OptionalCapability;
import nick1st.fancyvideo.api.helpers.capabilities.RequiresCapability;
import nick1st.fancyvideo.api.helpers.exceptions.MethodUnsupportedPlayerStateException;
import nick1st.fancyvideo.api.helpers.exceptions.MissingCapabilityException;

/**
 * This interface provides typical method existing on a player supporting audio.
 * @since 3.0.0
 */
public interface PlayerSupportsAudio {

    /**
     * Sets the volume of the audio out.
     * @param volume 0.0f or higher, with 1.0f being the original volume. Higher volume support depends on either the
     * {@link DefaultCapabilities#BOOST_VOLUME} or the {@link DefaultCapabilities#BOOST_VOLUME_PLUS}.
     * @throws IllegalArgumentException when a negative volume is provided.
     * @apiNote Implementations need to do the capability check themselves.
     * @since 3.0.0
     */
    @RequiresCapability(DefaultCapabilities.VOLUME)
    @OptionalCapability({DefaultCapabilities.BOOST_VOLUME, DefaultCapabilities.BOOST_VOLUME_PLUS})
    default void setVolume(float volume) {
        if (volume < 0.0f)
            throw new IllegalArgumentException("setVolume(float) requires a positive or zero number to be passed.");
    }

    /**
     * @return true if the current source has multiple audio tracks (e.g. different languages), false if not.
     * @throws MethodUnsupportedPlayerStateException if the player is currently in a state not supporting this method.
     * @since 3.0.0
     */
    boolean hasMultipleAudioTracks();

    /**
     * @return an array of available audio tracks
     * @throws MethodUnsupportedPlayerStateException if the player is currently in a state not supporting this method.
     * @since 3.0.0
     */
    String[] availableAudioTracks();

    /**
     * @return true if the current source has the specified audio track, false if not.
     * @param trackName the name to check for.
     * @throws MethodUnsupportedPlayerStateException if the player is currently in a state not supporting this method.
     * @since 3.0.0
     */
    boolean hasAudioTrack(String trackName);

    /**
     * @param trackName the name of the track to switch to.
     * @throws MethodUnsupportedPlayerStateException if the player is currently in a state not supporting this method.
     * @throws IllegalArgumentException if the track is not existing
     * @since 3.0.0
     */
    default void switchAudioTrack(String trackName) {
        if (!hasAudioTrack(trackName))
            throw new IllegalArgumentException("The track " + trackName +
                    " is not existent, as such switching to it is impossible");
    }

    /**
     * Converts the (stereo) input to a mono output.
     * Requires the {@link DefaultCapabilities#MONO_CONVERT} capability to be present on the player.
     * @since 3.0.0
     * @apiNote Implementations need to do the capability check themselves.
     * @throws MissingCapabilityException if the player does
     */
    @RequiresCapability(DefaultCapabilities.MONO_CONVERT)
    void convertToMono();
}

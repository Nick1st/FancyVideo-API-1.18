package nick1st.fancyvideo.api.helpers.formats;

import nick1st.fancyvideo.api.helpers.MediaType;
import org.jetbrains.annotations.ApiStatus;

import javax.print.attribute.standard.Media;
import java.util.Objects;
import java.util.SortedMap;

/**
 * A media format is a <b>BINDING CONTRACT</b> negotiated by different elements in the media pipeline(s) (e.g. sources,
 * sinks and filters). <br>
 * Please see the field javadocs for information on them.
 * @apiNote MediaFormats must be registered to their registry. // TODO Add a MediaFormat Registry
 * @apiNote This class is still experimental and may change between minor versions, if required to.
 * @since 3.0.0
 */
@ApiStatus.Experimental
public abstract class MediaFormat {
    /**
     * The media type this media format contract is for.
     * @since 3.0.0
     */
    public final MediaType type;

    /**
     * Base ctor
     * @param type the media type this format is for
     */
    protected MediaFormat(MediaType type) {
        this.type = type;
    }

    /**
     * Checks if a MediaFormat is compatible with another one. Compatible in this case has a special meaning:
     * <b>this</b> MediaFormat is a more generic one (e.g. does not require an exact resolution for example, instead
     * only provides sensible bounds), while the <b>compareTo</b> MediaFormat is an exactly specified, binding contract.
     * It is up to the format to correctly implement this. If this method returns <b>true</b>, it means that a player
     * supporting the format <b>MUST</b> be able to provide <b>EXACTLY</b> the <b>compareTo</b> format.
     * An implementation should make sure the rules it uses for its comparison are clearly specified.
     * @apiNote There is absolutely no guaranty that changing the object this is called on with the parameter will
     * return the same result.
     * @param compareTo the exact MediaFormat this Format should be compatible to
     * @return true if this format is compatible with the requested one, false otherwise
     * @since 3.0.0
     */
    public abstract boolean isCompatibleWith(MediaFormat compareTo);

    /**
     * Contains known media (sink) targets
     * @since 3.0.0
     */
    public static class KnownMediaTargets {
        /**
         * A sink that sinks into a byte buffer. The application receiving the buffer must take care of how to use it.
         * @apiNote Custom player implementations should try to always support this very basic sink format; Custom
         * formats should try to provide a converter from/to this sink format. // TODO Converter Registry
         * @since 3.0.0
         */
        public static final String BYTE_BUFFER = "byte_buffer";

        /**
         * A sink that creates a dynamic (MC) texture at the player's resource location.
         * The texture can be bound normally.
         * @since 3.0.0
         */
        public static final String TEXTURE = "texture";

    }
}

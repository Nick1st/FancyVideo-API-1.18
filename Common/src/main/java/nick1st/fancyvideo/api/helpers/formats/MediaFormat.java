package nick1st.fancyvideo.api.helpers.formats;

import nick1st.fancyvideo.api.helpers.MediaType;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

/**
 * A media format is a <b>BINDING CONTRACT</b> negotiated by different elements in the media pipeline(s) (e.g. sources,
 * sinks and filters). <br>
 * Please see the field javadocs for information on them.
 * @apiNote If you provide a custom MediaFormat please store it in a public static final field. Also contact me, so we
 * can discuss adding it as a known specification to further API releases.
 * @apiNote This class is still experimental and may change between minor versions, if required to.
 * @since 3.0.0
 */
@ApiStatus.Experimental
public class MediaFormat {
    /**
     * The media type this media format contract is for.
     * @since 3.0.0
     */
    public MediaType type;

    /**
     * The target this media format has. This is about how the data is stored and transitioned. For a video media type
     * this could e.g. be a GL_BUFFER, an int[] or some kind of pixel matrix.
     * @since 3.0.0
     */
    public String target;

    /**
     * Properties in the form of "key=value", concatenated by ";" (no spaces!, not trailing), sort alphabetically by the
     * name of the key. <br>
     * e.g. "alpha=true;byteOrder=ARGB"
     * @since 3.0.0
     */
    public String valuedProperties;

    public MediaFormat(MediaType mediaType, String targetType, String valuedProperties) {
        this.type = mediaType;
        this.target = targetType;
        this.valuedProperties = valuedProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaFormat that)) return false;
        return type == that.type && Objects.equals(target, that.target) && Objects.equals(valuedProperties, that.valuedProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, target, valuedProperties);
    }
}

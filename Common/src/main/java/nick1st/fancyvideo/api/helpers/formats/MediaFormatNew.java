package nick1st.fancyvideo.api.helpers.formats;

import io.netty.util.internal.UnstableApi;

import java.lang.reflect.Type;

/**
 * A media format is a <b>BINDING CONTRACT</b> negotiated by different elements in the media pipeline(s) (e.g. sources,
 * sinks and filters). <br>
 * Please see the field javadocs for information on them.
 * @apiNote Instantiable MediaFormats must be registered to their registry. // TODO Add a MediaFormat Registry
 * @apiNote This class is still experimental and as such unstable, meaning breaking changes can occur at any time if
 * necessary.
 * @since 3.0.0
 */
@UnstableApi
public abstract class MediaFormatNew<T extends MediaFormatNew<T, B>,
        B extends MediaFormatNew<B, B> & FormatConversion.BaseMediaFormat<B>>
        implements FormatConversion.BaseFormatConvert<T, B> {

    public final Type Type = MediaFormatNew.class;
}

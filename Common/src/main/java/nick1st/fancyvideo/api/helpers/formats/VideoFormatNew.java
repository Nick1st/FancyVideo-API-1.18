package nick1st.fancyvideo.api.helpers.formats;

import java.lang.reflect.Type;

/**
 * Base class for VideoFormats
 * @param <T> The format implemented by a subclass
 * @since 3.0.0
 */
public abstract class VideoFormatNew<T extends VideoFormatNew<T>> extends MediaFormatNew<T, ByteBufferVideoFormat> {
    public final Type Type = VideoFormatNew.class;
}

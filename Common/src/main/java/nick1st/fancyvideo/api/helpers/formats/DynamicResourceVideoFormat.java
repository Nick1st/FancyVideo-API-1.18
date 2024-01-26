package nick1st.fancyvideo.api.helpers.formats;

import java.lang.reflect.Type;

/**
 * A VideoFormat writing frames to a dynamic texture
 * @since 3.0.0
 */
public final class DynamicResourceVideoFormat extends VideoFormatNew<DynamicResourceVideoFormat> {

    public final Type Type = DynamicResourceVideoFormat.class;

    /**
     * Converts this format into the base format
     *
     * @return this format converted to a base format
     * @since 3.0.0
     */
    @Override
    public ByteBufferVideoFormat convertToBaseFormat() {
        return null;
    }

    /**
     * Converts the base format into this format
     *
     * @param baseFormat the base format to convert from
     * @return A (potentially new) instance of this format, with its values adapted to match the base
     * format.
     * @apiNote This might change and return the object it was called on, or return a new instance.
     * Better be safe than sorry and call it like this: <br> {@code T obj = new T();
     * obj = obj.convertFromBase(base);} <br>
     * It is intended that you <b>LOOSE REFERENCE</b> to the initial obj object!
     * @since 3.0.0
     */
    @Override
    public DynamicResourceVideoFormat convertFromBaseFormat(ByteBufferVideoFormat baseFormat) {
        return null;
    }
}

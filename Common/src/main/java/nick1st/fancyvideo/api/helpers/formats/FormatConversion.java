package nick1st.fancyvideo.api.helpers.formats;

/**
 * Helper class holding the interface required to allow from/to Base conversion
 * @since 3.0.0
 */
public final class FormatConversion {

    /**
     * Private ctor to hide the implicit public one
     * @since 3.0.0
     */
    private FormatConversion() {

    }

    /**
     * Interface providing default implementations for conversions calls on base formats.
     * Conversion calls on base formats don't have any effect, as such the default methods are very basic.
     * @param <B> the base format
     * @since 3.0.0
     */
    public interface BaseMediaFormat<B extends MediaFormatNew<B, B> & FormatConversion.BaseMediaFormat<B>> extends FormatConversion.BaseFormatConvert<B, B> {
        /**
         * Base to base conversion
         * @return the object it was called on. Equal to calling this on the object.
         * @since 3.0.0
         */
        @Override
        default B convertToBaseFormat() {
            //noinspection unchecked This unchecked cast should be fine
            return (B) this;
        }

        /**
         * Base to base conversion
         * @param baseFormat the base format to convert from
         * @return simply returns the param object
         * @apiNote This might change and return the object it was called on, or return a new instance.
         *          Better be safe than sorry and call it like this: <br> {@code T obj = new T();
         *          obj = obj.convertFromBase(base);} <br>
         *          It is intended that you <b>LOOSE REFERENCE</b> to the initial obj object!
         * @since 3.0.0
         */

        @Override
        default B convertFromBaseFormat(B baseFormat) {
            return baseFormat;
        }
    }

    /**
     * Interface providing conversion methods from/to base format
     * @param <T> this format type
     * @param <B> base format type
     * @since 3.0.0
     */
    public interface BaseFormatConvert<T extends MediaFormatNew<T, B>, B extends MediaFormatNew<B, B> & FormatConversion.BaseMediaFormat<B> > {
        /**
         * Converts this format into the base format
         * @return this format converted to a base format
         * @since 3.0.0
         */
        B convertToBaseFormat();

        /**
         * Converts the base format into this format
         * @param baseFormat the base format to convert from
         * @return A (potentially new) instance of this format, with its values adapted to match the base
         *         format.
         * @apiNote This might change and return the object it was called on, or return a new instance.
         *          Better be safe than sorry and call it like this: <br> {@code T obj = new T();
         *          obj = obj.convertFromBase(base);} <br>
         *          It is intended that you <b>LOOSE REFERENCE</b> to the initial obj object!
         * @since 3.0.0
         */
        T convertFromBaseFormat(B baseFormat);
    }

}

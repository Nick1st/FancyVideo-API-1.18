package nick1st.fancyvideo.api.helpers.formats;

/**
 * Enum specifying how a buffer size should be matched if source has a different size
 * @since 3.0.0
 */
public enum MatchSizeBy {
    /**
     * Fitting height, adding a padding on both sides
     * @since 3.0.0
     */
    FIT_HEIGHT,

    /**
     * Fitting width, adding a padding on top and on bottom
     * @since 3.0.0
     */
    FIT_WIDTH,

    /**
     * Fills the buffer, adding a padding right and bottom if source is smaller than buffer, crops source frames
     * otherwise
     * @since 3.0.0
     */
    FILL,

    /**
     * Does not accept a source that does not exactly match output width and height. Will return an error through the
     * error reporting pipeline // TODO Add an error reporting pipeline
     * @since 3.0.0
     */
    DROP_OTHER,

    /**
     * Stretches the source in x and/or y dimension to exactly match the target size. Can cause distortions.
     * @since 3.0.0
     */
    STRETCH
}

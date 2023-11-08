package nick1st.fancyvideo.api.helpers;

/**
 * The debug level specifies the amount of information generated during operation.
 * @since 3.0.0
 */
public enum DebugLevel {
    /**
     * Default debug level. Plugins should only log info and higher levels to the <b>PLUGIN</b> logger. // TODO Add a link to the logger
     * @since 3.0.0
     */
    INFO,
    /**
     * Plugins should log debug and higher levels to the <b>PLUGIN</b> logger. // TODO Add a link to the logger
     * @since 3.0.0
     */
    DEBUG,
    /**
     * Plugins should log debug and higher levels to the <b>PLUGIN</b> logger. // TODO Add a link to the logger
     * Additionally, other debug files (e.g. pipeline definitions, native debugging info, etc) should be generated in // TODO add directory
     * @since 3.0.0
     */
    COMPLEX_DEBUG,
    /**
     * Plugins should log all levels to the <b>PLUGIN</b> logger. // TODO Add a link to the logger
     * All additionally available debug information (e.g. pipeline definitions, native debugging info, etc) should be generated in // TODO add directory
     * Very verbose, might create massive amounts of data.
     * @since 3.0.0
     */
    TRACE
}

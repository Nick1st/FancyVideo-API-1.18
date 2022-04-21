package nick1st.fancyvideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Constants {
    //DLL Version
    public static final int DLL_VERSION = 0;
    public static final String PLUGINSDIR = "plugins/";

    // Mod info
    public static final String MOD_ID = "fancyvideo_api";
    public static final String MOD_NAME = "FancyVideo-API";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // System info
    public static String OS;
    public static String ARCH;

    // String constants
    public static final String AUDIO_OUTPUT = "audio_output";
    public static final String VIDEO_FILTER = "video_filter";
    public static final String ACCESS = "access";
}

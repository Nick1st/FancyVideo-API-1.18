package nick1st.fancyvideo;

import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.eventbus.EventPriority;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;
import nick1st.fancyvideo.internal.SimpleTextRenderer;
import nick1st.fancyvideo.internal.Util;

public record FancyVideoEvents() {

    public static final DynamicResourceLocation fallback = new DynamicResourceLocation(Constants.MOD_ID, "fallback");

    public static final String UNSUPPORTED = "It seems you're using an unsupported platform.";
    public static final String UNSUPPORTED2 = "Head to bit.ly/vlcBeta and try installing the latest version for your OS.";

    @FancyVideoEvent(priority = EventPriority.SURPREME)
    public static void addDefaultPlayer(PlayerRegistryEvent.AddPlayerEvent event) {
        event.handler().registerPlayerOnFreeResLoc(fallback, SimpleMediaPlayer.class);
        IntegerBuffer2D buffer = Util.injectableTextureFromJar("VLCMissing.png", FancyVideoEvent.class.getClassLoader(), 1024);

        buffer.bulkPut(SimpleTextRenderer.greatestSizedText(UNSUPPORTED, 1024, 310, -1), 0, 0, true); //1024 * 120
        buffer.bulkPut(SimpleTextRenderer.greatestSizedText(UNSUPPORTED2, 1024, 310, -1), 0, 710, true);

        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setIntBuffer(buffer);
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).renderToResourceLocation();
    }
}

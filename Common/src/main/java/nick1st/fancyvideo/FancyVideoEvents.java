package nick1st.fancyvideo;

import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.eventbus.EventPriority;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;
import nick1st.fancyvideo.internal.SimpleTextRenderer;
import nick1st.fancyvideo.internal.Util;

import java.awt.*;

public record FancyVideoEvents() {

    public static final DynamicResourceLocation fallback = new DynamicResourceLocation(Constants.MOD_ID, "fallback");

    public static final String UNSUPPORTED = "It seems you're using an unsupported platform.";
    public static final String UNSUPPORTED2 = "Head to 'https://nightlies.videolan.org/' and try installing the latest available version from there.";

    @FancyVideoEvent(priority = EventPriority.SURPREME)
    public static void addDefaultPlayer(PlayerRegistryEvent.AddPlayerEvent event) {
        event.handler().registerPlayerOnFreeResLoc(fallback, SimpleMediaPlayer.class);
        IntegerBuffer2D buffer = Util.injectableTextureFromJar("VLCMissing.png", FancyVideoEvent.class.getClassLoader(), 1024);
        buffer.bulkPut(SimpleTextRenderer.getInstance().drawString("Top", -1, 20), 0, 0, true);
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setIntBuffer(buffer);
        //((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setAdvancedFrame(Util.injectableTextureFromJar("assets/minecraft/textures/font/ascii.png", FancyVideoEvent.class.getClassLoader(), 128));
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).renderToResourceLocation();
        //SimpleTextRenderer.getInstance().drawString("Test", 0, 1.0F, 5);

        IntegerBuffer2D testBuffer = new IntegerBuffer2D(10, 10);
        testBuffer.fill(100, false);
        IntegerBuffer2D testBuffer2 = new IntegerBuffer2D(8, 8);
        testBuffer2.fill(50, false);
        testBuffer.bulkPut(testBuffer2, 1, 1, true);

        SimpleTextRenderer.greatestSizedText("This is a huge test text in a small matrix", 1024, 120);
    }
}

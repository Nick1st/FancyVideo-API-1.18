package nick1st.fancyvideo;

import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.eventbus.EventPriority;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;
import nick1st.fancyvideo.internal.SimpleTextRenderer;
import nick1st.fancyvideo.internal.Util;

public record FancyVideoEvents() {

    public static final DynamicResourceLocation fallback = new DynamicResourceLocation(Constants.MOD_ID, "fallback");

    @FancyVideoEvent(priority = EventPriority.SURPREME)
    public static void addDefaultPlayer(PlayerRegistryEvent.AddPlayerEvent event) {
        event.handler().registerPlayerOnFreeResLoc(fallback, SimpleMediaPlayer.class);
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setAdvancedFrame(Util.injectableTextureFromJar("VLCMissing.png", FancyVideoEvent.class.getClassLoader(), 1024));
        //((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setAdvancedFrame(Util.injectableTextureFromJar("assets/minecraft/textures/font/ascii.png", FancyVideoEvent.class.getClassLoader(), 128));
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).renderToResourceLocation();
        SimpleTextRenderer.getInstance().drawString("Test", 0, 1.0F, 5);
    }
}

package nick1st.fancyvideo;

import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.eventbus.EventPriority;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;

public record FancyVideoEvents() {

    public static final DynamicResourceLocation fallback = new DynamicResourceLocation(Constants.MOD_ID, "fallback");

    @FancyVideoEvent(priority = EventPriority.SURPREME)
    public static void addDefaultPlayer(PlayerRegistryEvent.AddPlayerEvent event) {
        event.handler().registerPlayerOnFreeResLoc(fallback, SimpleMediaPlayer.class);
    }
}

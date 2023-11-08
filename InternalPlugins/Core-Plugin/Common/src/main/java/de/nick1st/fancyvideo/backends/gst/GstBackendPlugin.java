package de.nick1st.fancyvideo.backends.gst;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupEvent;
import nick1st.fancyvideo.api.player.MediaPlayer;
import nick1st.fancyvideo.api.plugins.PluginLocator;

public class GstBackendPlugin implements PluginLocator {

    public static final String MODID = "fv-gst-plugin"; // TODO Wire up

    @Override
    public ResourceLocation identifier() {
        return new ResourceLocation(MODID, "backend");
    }

    @Override
    public void registerEvents(EnvironmentSetupEvent obj) {

    }

    @Override
    public Class<MediaPlayer>[] getProvidedPlayers() {
        return new Class[] {GstVideoPlayer.class};
    }
}

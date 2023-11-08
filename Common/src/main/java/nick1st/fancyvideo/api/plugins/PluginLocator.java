package nick1st.fancyvideo.api.plugins;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupEvent;
import nick1st.fancyvideo.api.player.MediaPlayer;

/**
 * This interface is loaded by a service loader. Each plugin needs to provide a service implementing this interface, for
 * it to be registered automatically. Services get loaded and instantiated during early mod loading.
 * @since 3.0.0
 */
public interface PluginLocator {
    /**
     * @return a ResourceLocation uniquely identifying this service. The mod id part should obviously be the plugins mod
     * id, the name path can be arbitrarily chosen (as long as it's unique in its namespace).
     * @since 3.0.0
     */
    ResourceLocation identifier();

    /**
     * This gets automatically called for any plugin that was located. Register your events here.
     * @param obj the obj to register at. // TODO Update javadoc
     * @since 3.0.0
     */
    void registerEvents(EnvironmentSetupEvent obj); // TODO Change the event to something more useful

    /**
     * This gets automatically called.
     * @return should return an array of classes extending {@link MediaPlayer}. These get automatically registered and
     * are queried for their properties whenever a player is created.
     * @since 3.0.0
     */
    Class<MediaPlayer>[] getProvidedPlayers();
}

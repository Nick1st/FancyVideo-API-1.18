package nick1st.fancyvideo.api.plugins;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.api.helpers.exceptions.plugin.IdentifierAlreadyKnownException;
import nick1st.fancyvideo.api.helpers.exceptions.plugin.PluginRegistryAlreadyFrozenException;
import nick1st.fancyvideo.api.player.PlayerFactoryHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class provides static access to all registered plugins. Use {@link #get()} to get the PluginRegistry.
 * @since 3.0.0
 */
public final class PluginRegistry {
    /**
     * Internal field holding the registry.
     * @since 3.0.0
     */
    private static final PluginRegistry REGISTRY = new PluginRegistry();

    /**
     * Statically gets the plugin registry.
     * @return the plugin registry
     * @since 3.0.0
     */
    public static PluginRegistry get() {
        return REGISTRY;
    }

    // Instance fields and methods below
    /**
     * Internal only.
     * @since 3.0.0
     */
    private final Map<ResourceLocation, Plugin> resourceLocationPluginMap = new HashMap<>();

    /**
     * Internal only.
     * @since 3.0.0
     */
    private boolean frozen = false;

    /**
     * Registers a plugin to the registry.
     * @param identifier the id of the plugin as a ResourceLocation
     * @param plugin the build plugin
     */
    public void register(ResourceLocation identifier, Plugin plugin) {
        if (frozen) throw new PluginRegistryAlreadyFrozenException();
        if (resourceLocationPluginMap.put(identifier, plugin) != null) {
            throw new IdentifierAlreadyKnownException();
        }
    }

    /**
     * Freezes the registry. You should not modify the registry after it has been frozen.
     * @since 3.0.0
     */
    public void freeze() {
        frozen = true;
    }

    /**
     *
     * @param query
     * @return
     * @since 3.0.0
     */
    public PlayerFactoryHelper getPlayerForQuery(Function<ResourceLocation, PlayerFactoryHelper> query) {
        if (!frozen) throw new RuntimeException("No players are available yet"); // TODO custom Exception
        // TODO javadoc
        // TODO implementation
        return new PlayerFactoryHelper(resourceLocationPluginMap.values().stream().findFirst().get().playersProvided[0], new ResourceLocation("test", "test"));
    }
}

package nick1st.fancyvideo.api.plugins;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.helpers.exceptions.plugin.IdentifierAlreadyKnownException;
import nick1st.fancyvideo.api.helpers.exceptions.plugin.PluginRegistryAlreadyFrozenException;
import nick1st.fancyvideo.api.player.MediaPlayer;
import nick1st.fancyvideo.api.player.PlayerFactoryHelper;
import nick1st.fancyvideo.api.player.querys.Query;

import javax.annotation.CheckForNull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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
     * Freezes the registry. You should not modify the registry after it has been frozen. You should not access the
     * registry before it is frozen.
     * @since 3.0.0
     */
    public void freeze() {
        frozen = true;
    }

    /**
     * Searches through the registry of registered players
     * @param query the query used for the search
     * @return a suitable MediaPlayer implementation, or null if none was found
     * @since 3.0.0
     */
    @CheckForNull
    public PlayerFactoryHelper getPlayerForQuery(Query query) {
        if (!frozen) throw new RuntimeException("No players are available yet"); // TODO custom Exception

        List<PlayerFactoryHelper> compatiblePlayerFactoryHelpers = new ArrayList<>();
        resourceLocationPluginMap.values().forEach(plugin -> Arrays.stream(plugin.playersProvided).forEach(playerClass -> {
            PlayerFactoryHelper factoryHelper = new PlayerFactoryHelper(playerClass);
            try (MediaPlayer testPlayer = playerClass.getConstructor(PlayerFactoryHelper.class, boolean.class)
                    .newInstance(factoryHelper, true)) {
                if (query.test(testPlayer)) compatiblePlayerFactoryHelpers.add(factoryHelper);
            } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                Constants.LOG.error("MediaPlayer implementation {} of plugin {} is missing default ctor.", playerClass.getName(), plugin.getClass().getName());
                Constants.LOG.error("Exception: ", e);
            } catch (Exception e) {
                Constants.LOG.error("An error occurred during evaluation of query: ", e);
            }
        }));
        compatiblePlayerFactoryHelpers.forEach(helper ->
                Constants.LOG.debug("Found a compatible player implementation for a query: {}",
                        helper.getClassToInstantiate().getName()));
        return !compatiblePlayerFactoryHelpers.isEmpty() ? compatiblePlayerFactoryHelpers.get(0) : null;
    }
}

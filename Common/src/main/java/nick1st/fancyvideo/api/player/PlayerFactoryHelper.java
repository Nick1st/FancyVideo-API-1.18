package nick1st.fancyvideo.api.player;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.helpers.DebugLevel;

import java.util.HashMap;
import java.util.Map;

/**
 * This is passed to the player factory to init the player. If some info from player search (e.g. a requested capability
 * is required to be known in order to create the player in a way that it can provide this capability).
 * @apiNote The player creates this object during the player search query
 * @since 3.0.0
 */
public class PlayerFactoryHelper {

    /**
     * Players should add required information to instantiate themselves into this map during player search.
     * @since 3.0.0
     */
    public Map<String, Object> paramList = new HashMap<>();

    private Class<MediaPlayer> classToInstantiate;

    private ResourceLocation playerResourceLocation;

    private DebugLevel debugLevel = DebugLevel.COMPLEX_DEBUG; // TODO get this from the main config

    /**
     * @param classToInstantiate the class of player that should be created
     * @param playerResourceLocation the resource location uniquely identifying this player, once/if it is instantiated.
     *                               Even if the player gets disposed later, you can <b>NOT</b> reuse this!
     * @since 3.0.0
     */
    public PlayerFactoryHelper(Class<MediaPlayer> classToInstantiate, ResourceLocation playerResourceLocation) {
        this.classToInstantiate = classToInstantiate;
        this.playerResourceLocation = playerResourceLocation;
        // TODO check resource location npt already created
    }

    /**
     * @return The debug level set in config
     * @since 3.0.0
     */
    public DebugLevel getDebugLevel() {
        return debugLevel;
    }

    /**
     * @return the class of a player this info is for
     * @since 3.0.0
     */
    public Class<MediaPlayer> getClassToInstantiate() {
        return classToInstantiate;
    }

    /**
     * @return the resource location the player will later <b>SELF-REGISTER</b> on.
     * @since 3.0.0
     */
    public ResourceLocation getPlayerResourceLocation() {
        return playerResourceLocation;
    }

    /**
     * Instantiates a MediaPlayer specified by this PlayerFactoryHelper and claims the ResourceLocation specified.
     * @return a mediaPlayer
     * @since 3.0.0
     */
    public MediaPlayer create() {
        // TODO register ResourceLocation to a registry
        // TODO also register the player, so that we certainly can clear it up
        try {
            return classToInstantiate.getDeclaredConstructor(this.getClass()).newInstance(this);
        } catch (Exception e) {
            // TODO Exceptions etc
            return null;
        }
    }
}

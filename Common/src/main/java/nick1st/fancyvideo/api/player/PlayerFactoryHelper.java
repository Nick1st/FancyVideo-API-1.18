package nick1st.fancyvideo.api.player;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.api.helpers.DebugLevel;
import nick1st.fancyvideo.api.helpers.capabilities.DefaultCapabilities;
import nick1st.fancyvideo.api.player.querys.LogicQueries;
import nick1st.fancyvideo.api.player.querys.MediaPlayerQueries;

import javax.annotation.CheckForNull;
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

    @CheckForNull
    private ResourceLocation playerResourceLocation;

    private DebugLevel debugLevel = DebugLevel.COMPLEX_DEBUG; // TODO get this from the main config

    /**
     * @param classToInstantiate the class of the player that should be created
     * @since 3.0.0
     */
    public PlayerFactoryHelper(Class<MediaPlayer> classToInstantiate) {
        this.classToInstantiate = classToInstantiate;
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
     * @param playerResourceLocation the resource location uniquely identifying this player, once/if it is instantiated.
     *                               Even if the player gets disposed later, you can <b>NOT</b> reuse this!
     * @return a mediaPlayer
     * @since 3.0.0
     */
    public MediaPlayer create(ResourceLocation playerResourceLocation) {
        // TODO register ResourceLocation to a registry
        // TODO also register the player, so that we certainly can clear it up
        // TODO check resource location not already created
        try {
            this.playerResourceLocation = playerResourceLocation;
            MediaPlayer mediaPlayer = classToInstantiate.getDeclaredConstructor(this.getClass(), Boolean.class).newInstance(this, false);
            return mediaPlayer;
        } catch (Exception e) {
            // TODO Exceptions etc
            return null;
        }
    }
}

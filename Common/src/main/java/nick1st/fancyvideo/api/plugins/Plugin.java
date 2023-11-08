package nick1st.fancyvideo.api.plugins;

import nick1st.fancyvideo.api.player.MediaPlayer;

// TODO javadoc
// TODO TODO TODO
public class Plugin {
    public final Class<MediaPlayer>[] playersProvided;

    public Plugin(Class<MediaPlayer>[] playersProvidedByPlugin) {
        playersProvided = playersProvidedByPlugin;
    }
}

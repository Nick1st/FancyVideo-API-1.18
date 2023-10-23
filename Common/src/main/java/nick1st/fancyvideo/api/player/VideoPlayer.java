package nick1st.fancyvideo.api.player;

import nick1st.fancyvideo.api.mediaPlayer.MediaPlayerBase;

/**
 * Abstract VideoPlayer
 *
 * @see MediaPlayer
 * @since 0.2.0.0
 */
public abstract class VideoPlayer {

    public abstract void play();

    public abstract void pause();

    public abstract void setVolume();


}

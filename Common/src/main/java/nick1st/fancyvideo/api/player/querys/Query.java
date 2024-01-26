package nick1st.fancyvideo.api.player.querys;

import nick1st.fancyvideo.api.player.MediaPlayer;

import java.util.function.Predicate;

@FunctionalInterface
public interface Query extends Predicate<MediaPlayer> {
}

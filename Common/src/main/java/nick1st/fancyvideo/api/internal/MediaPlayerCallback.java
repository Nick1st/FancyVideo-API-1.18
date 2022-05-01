/*
 * This file is part of the FancyVideo-API.
 *
 * The FancyVideo-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The FancyVideo-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The FancyVideo-API uses VLCJ, Copyright 2009-2021 Caprica Software Limited,
 * licensed under the GNU General Public License.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You should have received a copy of the GNU General Public License
 * along with FancyVideo-API.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 Nick1st.
 */

package nick1st.fancyvideo.api.internal; //NOSONAR

import nick1st.fancyvideo.api.mediaPlayer.MediaPlayerBase;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.RenderCallbackAdapter;

/**
 * This provides a simple RenderCallbackAdapter implementation
 *
 * @since 0.1.0.0
 */
public class MediaPlayerCallback extends RenderCallbackAdapter {
    private final MediaPlayerBase mediaPlayer;
    private int width;

    public MediaPlayerCallback(int width, MediaPlayerBase mediaPlayer) {
        this.width = width;
        this.mediaPlayer = mediaPlayer;
    }

    public void setBuffer(AdvancedFrame buffer) {
        this.width = buffer.getWidth();
        setBuffer(buffer.getFrame());
    }

    @Override
    protected void onDisplay(uk.co.caprica.vlcj.player.base.MediaPlayer mediaPlayer, int[] buffer) {
        this.mediaPlayer.setAdvancedFrame(new AdvancedFrame(buffer, width));
    }
}

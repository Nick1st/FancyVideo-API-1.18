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

package nick1st.fancyvideo; //NOSONAR

import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.eventbus.EventPriority;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;
import nick1st.fancyvideo.internal.SimpleTextRenderer;
import nick1st.fancyvideo.internal.Util;

public record FancyVideoEvents() {

    public static final DynamicResourceLocation fallback = new DynamicResourceLocation(Constants.MOD_ID, "fallback");

    public static final String UNSUPPORTED = "It seems you're using an unsupported platform.";
    public static final String UNSUPPORTED2 = "Head to bit.ly/vlcBeta and try installing the latest version for your OS.";

    @FancyVideoEvent(priority = EventPriority.SURPREME)
    @SuppressWarnings("unused")
    public static void addDefaultPlayer(PlayerRegistryEvent.AddPlayerEvent event) {
        event.handler().registerPlayerOnFreeResLoc(fallback, SimpleMediaPlayer.class);
        IntegerBuffer2D buffer = Util.injectableTextureFromJar("VLCMissing.png", FancyVideoEvent.class.getClassLoader(), 1024);

        buffer.bulkPut(SimpleTextRenderer.greatestSizedText(UNSUPPORTED, 1024, 310, -1), 0, 0, true); //1024 * 120
        buffer.bulkPut(SimpleTextRenderer.greatestSizedText(UNSUPPORTED2, 1024, 310, -1), 0, 710, true);

        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).setIntBuffer(buffer);
        ((SimpleMediaPlayer) event.handler().getMediaPlayer(fallback)).renderToResourceLocation();
    }
}

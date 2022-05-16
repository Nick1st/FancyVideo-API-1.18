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

package nick1st.fancyvideo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record Constants() {
    //DLL Version
    public static final int DLL_VERSION = 1;
    public static final String PLUGINSDIR = "plugins/";

    // Mod info
    public static final String MOD_ID = "fancyvideo_api";
    public static final String MOD_NAME = "FancyVideo-API";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    // System info
    public static String OS;
    public static String ARCH;

    // First render Tick
    public static boolean renderTick;

    // String constants
    public static final String AUDIO_OUTPUT = "audio_output";
    public static final String VIDEO_FILTER = "video_filter";
    public static final String ACCESS = "access";

    // No_Library_Mode
    public static boolean NO_LIBRARY_MODE;
    public static final boolean DEBUG_NO_LIBRARY_MODE = false;
}

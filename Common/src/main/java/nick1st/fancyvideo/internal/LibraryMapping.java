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

package nick1st.fancyvideo.internal; //NOSONAR

import nick1st.fancyvideo.Constants;

public enum LibraryMapping {
    // core
    libVLC("libvlc", false),
    libVLCCore("libvlccore", false),

    // audio_filter
    libEqualizer("audio_filter", "libequalizer_plugin"),

    // audio_output
    libADummy(Constants.AUDIO_OUTPUT, "libadummy_plugin"),
    libAMem(Constants.AUDIO_OUTPUT, "libamem_plugin"),
    libWaveout(Constants.AUDIO_OUTPUT, "libwaveout_plugin"),

    // logger
    libConsoleLogger("logger", "libconsole_logger_plugin"),
    libFileLogger("logger", "libfile_logger_plugin"),

    // spu
    libLogo("spu", "liblogo_plugin"),
    libMarq("spu", "libmarq_plugin"),

    // video_filter // TODO: Find out if we need all of those
    libAdjust(Constants.VIDEO_FILTER, "libadjust_plugin"),
    libAlphaMask(Constants.VIDEO_FILTER, "libalphamask_plugin"),
    libDeinterlace(Constants.VIDEO_FILTER, "libdeinterlace_plugin"),
    libFPS(Constants.VIDEO_FILTER, "libfps_plugin"),

    // video_output
    libVDummy("video_output", "libwdummy_plugin"),
    libVMem("video_output", "libvmem_plugin"),

    // video_chroma
    //libI420RGB("video_chroma", "libi420_rgb_plugin"),
    libSWScale("video_chroma", "libswscale_plugin"),

    // access
    libFilesystem(Constants.ACCESS, "libfilesystem_plugin"),
    libHttp(Constants.ACCESS, "libhttp_plugin"),
    libHttps(Constants.ACCESS, "libhttps_plugin"),

    // misc
    libTLS("misc", "libgnutls_plugin"),

    // codec
    libAVCodec("codec", "libavcodec_plugin");


    public final String windowsName;
    public final String linuxName;
    public final String macName;
    final boolean isPlugin;

    LibraryMapping(String windowsName, String linuxName, String macName, boolean isPlugin) {
        this.windowsName = windowsName;
        this.linuxName = linuxName;
        this.macName = macName;
        this.isPlugin = isPlugin;
    }

    LibraryMapping(String windowsName, String linuxName, String macName) {
        this(windowsName, linuxName, macName, true);
    }

    LibraryMapping(String simpleName, boolean isPlugin) {
        this(simpleName + ".dll", simpleName + ".so", simpleName + ".dylib", isPlugin);
    }

    LibraryMapping(String prefix, String simpleName) {
        this(prefix + "/" + simpleName, true);
    }

    LibraryMapping(String simpleName) {
        this(simpleName, true);
    }

     public String getByOS(String os) {
         return switch (os) {
             case "windows" -> windowsName;
             case "mac" -> macName;
             case "linux" -> linuxName;
             default -> throw new UnsupportedOperationException("Invalid OS");
         };
     }
}


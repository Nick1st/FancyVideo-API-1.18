package nick1st.fancyvideo.natives.api;

import java.io.File;
import java.util.Set;

/**
 * This enum lists known NativeGroups. If you want a new native group being added,
 * please message Nick1st.
 *
 * @since 3.0.0
 */
public enum NativeGroup {

    /**
     * The <b>BASE</b> group contains always required plugins.
     * @since 3.0.0
     */
    BASE(Set.of(
        new NativeListEntry("libvlc", new File("")),
        new NativeListEntry("libvlccore", new File("")),
        new NativeListEntry("audio_filter/libequalizer_plugin"),
        new NativeListEntry("audio_output/libwaveout_plugin"),
        new NativeListEntry("codec/libavcodec_plugin"),
        new NativeListEntry("codec/libedummy_plugin"),
        new NativeListEntry("logger/libconsole_logger_plugin"),
        new NativeListEntry("logger/libfile_logger_plugin"),
        new NativeListEntry("misc/libgnutls_plugin"),
        new NativeListEntry("video_chroma/libswscale_plugin"),
        new NativeListEntry("video_filter/libdeinterlace_plugin"),
        new NativeListEntry("video_output/libvmem_plugin"),
        new NativeListEntry("video_output/libwdummy_plugin")
    ));

    NativeGroup(Set<NativeListEntry> moduleSet) {
        modules = moduleSet;
    }

    /**
     *
     * @return A set of modules in this group.
     * @since 3.0.0
     */
    public Set<NativeListEntry> getModules() {
        return modules;
    }

    final Set<NativeListEntry> modules;
}

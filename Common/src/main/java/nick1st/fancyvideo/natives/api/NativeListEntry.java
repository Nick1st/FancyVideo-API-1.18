package nick1st.fancyvideo.natives.api;

import nick1st.fancyvideo.Constants;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Objects;

/**
 * Describes a native vlc module.
 * @since 3.0.0
 */
public class NativeListEntry {
    private final String name;
    private final File path;


    /**
     *
     * @param name Name of the module. For plugins this should be pluginType/pluginFile without file extension.
     * @since 3.0.0
     */
    public NativeListEntry(@Nonnull String name) {
        this.name = name;
        this.path = new File(Constants.PLUGINSDIR);
    }

    /**
     *
     * @param name Name of the module. For plugins this should be pluginType/pluginFile without file extension.
     * @param path Path of the module. This is extended by the name.
     * @since 3.0.0
     */
    public NativeListEntry(@Nonnull String name, @Nonnull File path) {
        this.name = name;
        this.path = path;
    }

    /**
     *
     * @return The path of this module on disk.
     * @since 3.0.0
     */
    public File getPath() {
        return new File(path, name);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NativeListEntry that = (NativeListEntry) o;
        return name.equals(that.name) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}

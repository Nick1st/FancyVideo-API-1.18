package nick1st.fancyvideo.natives.api;

import nick1st.fancyvideo.Constants;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Objects;

/**
 * This class provides some convenience when comparing native modules. It kind of describes some properties of it.
 * @since 3.0.0
 */
public class NativeModuleRepresentation {
    private final String name;
    private final File path;


    /**
     * @param name Name of the module. For default vlc plugins this should be
     *             {@link Constants#PLUGINSDIR}/pluginType/pluginFile without file extension.
     * @deprecated Use {@link NativeModuleRepresentation#NativeModuleRepresentation(String, File)}  instead.
     * @since 3.0.0
     */
    @Deprecated(since = "3.0.0")
    public NativeModuleRepresentation(@Nonnull String name) {
        String[] split = name.split("/", 2);
        this.name = split[1];
        this.path = new File(split[0]);
    }

    /**
     *
     * @param name Name of the module. For plugins this should be pluginType/pluginFile without file extension.
     * @param path Path of the module. This is extended by the name. For default vlc modules this should be
     *             {@link Constants#PLUGINSDIR}.
     * @since 3.0.0
     */
    public NativeModuleRepresentation(@Nonnull String name, @Nonnull File path) {
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

    public String getIdentifier() {
        if (path.getName().isBlank()) {
            return name;
        } else {
            return path.getName() + "/" + name;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NativeModuleRepresentation that = (NativeModuleRepresentation) o;
        return name.equals(that.name) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }
}

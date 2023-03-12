package nick1st.fancyvideo.api_consumer.natives;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A java object representation of a native vlc module.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class ModulePOJO {
    private final String moduleIdentifier;
    private final ModuleType moduleType;

    /**
     * @param moduleIdentifier The unique identifier of a module. Should be the default path in modular installation (on
     *                         Windows). If this module does not have a modular install (e.g. a Linux only module) then
     *                         the identifier should be chosen based upon where the module would most likely end up on
     *                         Windows.
     * @since 3.0.0
     * @see ModulePOJO
     */
    public ModulePOJO(@Nonnull String moduleIdentifier) {
        this.moduleIdentifier = moduleIdentifier;
        this.moduleType = ModuleType.getModuleType(moduleIdentifier);
    }

    /**
     * Get the simple name of this module.
     * @return The simple name of this module. This is the filename without file ending.
     * @since 3.0.0
     */
    public String getName() {
        String[] pathComponents = moduleIdentifier.split("/");
        String lastPathComponent = pathComponents[pathComponents.length - 1];
        int dotIndex = lastPathComponent.lastIndexOf(".");
        if (dotIndex != -1) {
            return lastPathComponent.substring(0, dotIndex);
        } else {
            return lastPathComponent;
        }
    }

    /**
     * @return The identifier of this module
     * @since 3.0.0
     */
    public String getModuleIdentifier() {
        return moduleIdentifier;
    }

    /**
     * @return The type of this module
     * @since 3.0.0
     */
    public ModuleType getModuleType() {
        return moduleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModulePOJO modulePOJO = (ModulePOJO) o;
        return getModuleIdentifier().equals(modulePOJO.getModuleIdentifier()) && getModuleType() == modulePOJO.getModuleType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getModuleIdentifier(), getModuleType());
    }

    @Override
    public String toString() {
        return "ModuleObj{" +
                "moduleIdentifier='" + moduleIdentifier + '\'' +
                ", moduleType=" + moduleType +
                '}';
    }


    /**
     * Using ModuleTypes we try to guess what a module is for. This should theoretically make it possible for us to
     * make well-informed guesses upon certain errors.
     * @since 3.0.0
     * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
     */
    public enum ModuleType {
        ROOT,
        PLUGIN_SPECIAL,
        SPECIAL,
        access,
        access_output,
        audio_filter,
        audio_mixer,
        audio_output,
        codec,
        control,
        d3d9,
        d3d11,
        demux,
        gui,
        keystore,
        logger,
        lua,
        meta_engine,
        misc,
        mux,
        nvdec,
        packetizer,
        services_discovery,
        spu,
        stream_extractor,
        stream_filter,
        stream_out,
        text_renderer,
        video_chroma,
        video_filter,
        video_output,
        video_splitter,
        visualization;

        /**
         * Guesses the ModuleType based on the moduleIdentifier.
         * @param moduleIdentifier The string identifying a module.
         * @return The guessed ModuleType.
         * @since 3.0.0
         */
        public static ModuleType getModuleType(@Nonnull String moduleIdentifier) {
            String[] pathComponents = moduleIdentifier.split("/");
            if (pathComponents.length == 1) {
                return ROOT;
            } else if (!Objects.equals(pathComponents[0], "plugins")) {
                return SPECIAL;
            }
            switch (pathComponents[1]) { // Use a switch statement, so we can further specify categories if we want to.
                default:
                    try {
                        return valueOf(pathComponents[1]);
                    } catch (IllegalArgumentException e) {
                        return PLUGIN_SPECIAL;
                    }
            }
        }
    }
}

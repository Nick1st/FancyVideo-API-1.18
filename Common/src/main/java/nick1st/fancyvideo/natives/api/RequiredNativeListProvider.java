package nick1st.fancyvideo.natives.api;

import javax.annotation.Nullable;

/**
 * Dependencies should implement this to provide a list of required modules. <br>
 * @see nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupedEvent
 * @since 3.0.0
 */
public interface RequiredNativeListProvider extends NativeListProviders {

    /**
     * @return The modules provided by this service.
     * @since 3.0.0
     */
    @Override
    @Nullable
    NativeListEntry[] getModules();

    /**
     * @return A list of {@link NativeGroup}s provided by this service.
     * @since 3.0.0
     */
    @Override
    @Nullable
    NativeGroup[] getModuleGroups();

    /**
     * If failing to provide a module on this list, normally API loading will continue. <br>
     * If this returns true and a module fails, the API will run in {@link nick1st.fancyvideo.Constants#NO_LIBRARY_MODE}
     * instead.
     * @return True if this list causes global failure, false otherwise.
     */
    default boolean failGlobally() {
        return false;
    }
}

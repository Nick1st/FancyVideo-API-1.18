package nick1st.fancyvideo.natives.api;

import javax.annotation.Nullable;

/**
 * Base interface of service loader based native loading.
 * @since 3.0.0
 */
public interface NativeListProviders {
    @Nullable
    NativeListEntry[] getModules();

    @Nullable
    NativeGroup[] getModuleGroups();

    /**
     * If there are multiple lists matching for a task, the onw with a higher priority is picked first.
     * @since 3.0.0
     */
    short priority = 100;
}

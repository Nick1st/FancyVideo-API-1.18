package nick1st.fancyvideo.natives.api;

import javax.annotation.Nullable;

/**
 * Base interface of service loader based native loading.
 * @since 3.0.0
 */
public interface NativeListProviders {
    NativeListEntry[] getModules();

    /**
     * If there are multiple lists matching for a task, the one with a higher priority is picked first.
     * @since 3.0.0
     */
    default short priority() {
        return 10;
    }
}

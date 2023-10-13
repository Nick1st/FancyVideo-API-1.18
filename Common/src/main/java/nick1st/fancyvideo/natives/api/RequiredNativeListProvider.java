package nick1st.fancyvideo.natives.api;

/**
 * Dependencies should implement this to provide a list of required modules. <br>
 * @see nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupedEvent
 * @since 3.0.0
 */
public interface RequiredNativeListProvider extends NativeListProviders {

    /**
     * @return The modules required by this service.
     * @since 3.0.0
     */
    @Override
    NativeListEntry[] getModules();

    /**
     * If failing to provide a module on this list, normally API loading will continue. <br>
     * If this returns true and a module fails, the API will run in {@link nick1st.fancyvideo.Constants#NO_LIBRARY_MODE}
     * instead.
     * @return True if this list causes global failure, false otherwise.
     */
    default boolean failGlobally() {
        return false;
    }

    /**
     * The type of requirement this is
     * @return {@link RequireState#REQUIRED} if the API should run in
     * {@link nick1st.fancyvideo.Constants#NO_LIBRARY_MODE} if this module fails.
     * Else returns {@link RequireState#OPTIONAL}
     * @since 3.0.0
     */
    default RequireState requirementType() {
        return RequireState.OPTIONAL;
    }
}

package nick1st.fancyvideo.api.eventbus.event;

import nick1st.fancyvideo.natives.api.NativeListEntry;

import java.util.Set;

/**
 * This event is fired after api setup. <br>
 * Query {@link EnvironmentSetupedEvent#unavailable} to find out what requested modules could not be loaded. <br>
 * Querying {@link nick1st.fancyvideo.Constants#NO_LIBRARY_MODE} is safe after this event fired.
 *
 * @since 3.0.0
 */
public class EnvironmentSetupedEvent extends Event{

    /**
     * This set is immutable.
     * @since 3.0.0
     */
    public final Set<NativeListEntry> unavailable;

    /**
     *
     * @param unavailable A set containing unavailable modules
     * @since 3.0.0
     */
    public EnvironmentSetupedEvent(Set<NativeListEntry> unavailable) {
        this.unavailable = unavailable;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }
}

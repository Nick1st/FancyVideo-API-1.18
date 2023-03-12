package nick1st.fancyvideo.api_consumer;

import javax.annotation.Nonnull;

/**
 * This interface is loaded by a service loader. Each API Consumer needs to provide a Service extending and implementing
 * this. Those services get loaded during mod instantiation.
 * @since 3.0.0
 */
public interface ApiConsumer {

    /**
     * The consumer id needs to be a unique String. It is used to identify this api consumer and for displaying
     * information in logs. Because of this, I strongly recommend using your modid.
     * @return The consumer id of this api consumer.
     * @since 3.0.0
     */
    @Nonnull
    String getConsumerId(); // TODO

    /**
     * This provides an option to register a set of classes to the eventbus in a very early loading stage. This should
     * be used as the main entry for registering more events in such an early event
     * (e.g. {@link nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupedEvent})
     * @return An array of classes that contain static api eventbus subscribers
     * @since 3.0.0
     */
    @Nonnull
    Class<?>[] registerEvents(); // TODO (Maybe change to object to allow for non statics?)

    /**
     * Native providers are classes that can provide native vlc modules to the pool of available modules.
     * @since 3.0.0
     */
    Class<?>[] nativeProviders();
}

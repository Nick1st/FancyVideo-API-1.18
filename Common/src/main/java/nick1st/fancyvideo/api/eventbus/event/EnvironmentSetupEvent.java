package nick1st.fancyvideo.api.eventbus.event;

import nick1st.fancyvideo.api_consumer.ConsumerHandler;

public class EnvironmentSetupEvent {

    /**
     * This event is used to supply known ModuleGroups (and their representation) so that their Holders can be resolved
     * later on. Should mainly be used by Providers or if you don't want to type the same structure twice.
     */
    public static class ModuleGroups extends Event {

        public ModuleGroups() {
            super();
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    public static class RegisterRequests extends Event {
        public final ConsumerHandler.RequestRegistry REGISTRY;

        public RegisterRequests(ConsumerHandler.RequestRegistry requestRegistry) {
            super();
            REGISTRY = requestRegistry;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    public static class RegisterProviders extends Event {
        public final ConsumerHandler.ProviderRegistry REGISTRY;

        public RegisterProviders(ConsumerHandler.ProviderRegistry providerRegistry) {
            super();
            REGISTRY = providerRegistry;
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }
}

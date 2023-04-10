package nick1st.fancyvideo.api_consumer;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupEvent;
import nick1st.fancyvideo.api_consumer.natives.ModuleHolder;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleGroup;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleSingle;
import nick1st.fancyvideo.api_consumer.provider.Provider;
import nick1st.fancyvideo.api_consumer.requester.Request;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class TestConsumer implements ApiConsumer {
    @NotNull
    @Override
    public String getConsumerId() {
        return "TestApiConsumer";
    }

    @NotNull
    @Override
    public Class<?>[] registerEvents() {
        return new Class<?>[]{TestConsumer.class};
    }

    @Override
    public Class<?>[] nativeProviders() {
        return new Class[0];
    }


    @FancyVideoEvent
    public static void registerModuleGroups(EnvironmentSetupEvent.ModuleGroups event) {
        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID + ":groups/core"))
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules:libvlccore")).build())
                .build();

        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID + ":groups/test"))
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules:plugin/video_output/testout")).build())
                .build();
    }

    @FancyVideoEvent
    public static void registerRequests(EnvironmentSetupEvent.RegisterRequests event) {
        event.REGISTRY.add(
            new Request(new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID, "groups/base"))
                .contain(new ModuleHolder(new ResourceLocation(Constants.MOD_ID + ":groups/core")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build())
                .build(),
                "testmod"
            )
        );

        event.REGISTRY.add(new Request(new ModuleHolder(new ResourceLocation(Constants.MOD_ID + ":groups/test")).build(), "testmod"));
    }

    @FancyVideoEvent
    public static void registerProviders(EnvironmentSetupEvent.RegisterProviders event) {
        event.REGISTRY.add(
                new Provider() {
                    @Override
                    public ResourceLocation providerName() {
                        return new ResourceLocation("testmod", "testprovider");
                    }

                    @Override
                    public ResourceLocation[] providedModules() {
                        Set<ResourceLocation> providedModules = new HashSet<>();
                        providedModules.add(new MutableModuleSingle(new ResourceLocation("vlc_modules:libvlc")).build());
                        providedModules.add(new MutableModuleSingle(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build());
                        return providedModules.toArray(new ResourceLocation[0]);
                    }

                    @Override
                    public boolean isValid() {
                        return true;
                    }
                }
        );

        event.REGISTRY.add(
                new Provider() {
                    @Override
                    public ResourceLocation providerName() {
                        return new ResourceLocation("testmod", "testprovider2");
                    }

                    @Override
                    public ResourceLocation[] providedModules() {
                        Set<ResourceLocation> providedModules = new HashSet<>();
                        providedModules.add(new MutableModuleSingle(new ResourceLocation("vlc_modules:libvlc")).build());
                        providedModules.add(new MutableModuleSingle(new ResourceLocation("vlc_modules:libvlccore")).build());
                        providedModules.add(new MutableModuleSingle(new ResourceLocation("vlc_modules:libvlclul")).build());
                        return providedModules.toArray(new ResourceLocation[0]);
                    }

                    @Override
                    public boolean isValid() {
                        return true;
                    }

                    @Override
                    public boolean isGroupProvider() {
                        return true;
                    }
                }
        );
    }
}

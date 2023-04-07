package nick1st.fancyvideo.api_consumer;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupEvent;
import nick1st.fancyvideo.api_consumer.natives.ModuleHolder;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleGroup;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleSingle;
import nick1st.fancyvideo.api_consumer.requester.Request;
import org.jetbrains.annotations.NotNull;

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


    public static void registerModuleGroups(EnvironmentSetupEvent.ModuleGroups event) {
        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID + ":groups/core"))
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules:libvlccore")).build())
                .build();
    }

    @FancyVideoEvent
    public static void registerRequests(EnvironmentSetupEvent.RegisterRequests event) {
        event.REGISTRY.add(
            new Request(new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID, "groups/base"))
                .contain(new ModuleHolder(new ResourceLocation(Constants.MOD_ID + ":groups/core")).build())
                .contain(new ModuleHolder(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build())
                .build(),
                "testmod"
            )
        );
    }
}

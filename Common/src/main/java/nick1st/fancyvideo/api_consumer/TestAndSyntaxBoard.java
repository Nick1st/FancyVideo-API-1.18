package nick1st.fancyvideo.api_consumer;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api_consumer.natives.ModuleHolder;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleGroup;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleSingle;
import nick1st.fancyvideo.api_consumer.requester.Request;

public class TestAndSyntaxBoard {
    public static void main(String[] args) {

        // Request a ModuleGroup
        Request.New(new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID, "groups/base"))
                .contain(new ModuleHolder(new ResourceLocation(Constants.MOD_ID + ":groups/core")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build())
                .build()
        );

        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID + ":groups/core"))
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlccore")).build())
                .build();

        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID, "groups/basedouble"))
                .contain(new ModuleHolder(new ResourceLocation("test:groups/double")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build())
                .build();

        new MutableModuleGroup(new ResourceLocation("test:groups/double"))
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build())
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlccore")).build())
                .build();

        Request.ModuleRequestGraph.compileModuleLikeRegistry();
        Request.ModuleRequestGraph.buildAllRequests();
        System.out.println("Tests finished");
        return;
    }
}
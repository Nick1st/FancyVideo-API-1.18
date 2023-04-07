package nick1st.fancyvideo.api_consumer;

import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api_consumer.natives.ModuleHolder;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleGroup;
import nick1st.fancyvideo.api_consumer.natives.MutableModuleSingle;
import nick1st.fancyvideo.api_consumer.requester.Request;

public class TestAndSyntaxBoard {
    public static void main(String[] args) {


        new MutableModuleSingle(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).isFeature(true)
                .build();
        // Request a ModuleGroup
        Request.New(new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID, "groups/base"))
                .contain(new ModuleHolder(new ResourceLocation(Constants.MOD_ID + ":groups/core")).build())
                .contain(new ModuleHolder(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build())
                .isFeature(true)
                .build()
        );

        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID + ":groups/core"))
                .contain(new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build())
                .contain(new ModuleHolder(new ResourceLocation("vlc_modules:libvlccore")).build())
                .build();

        new MutableModuleGroup(new ResourceLocation("test:groups/double"))
                .contain(new ModuleHolder(new ResourceLocation("vlc_modules:libvlc")).build())
                .contain(new ModuleHolder(new ResourceLocation("vlc_modules:libvlccore")).build())
                .build();

        new MutableModuleGroup(new ResourceLocation(Constants.MOD_ID, "groups/basedouble"))
                .contain(new ModuleHolder(new ResourceLocation("test:groups/double")).build())
                .contain(new ModuleHolder(new ResourceLocation("vlc_modules", "plugin/video_output/testout")).build())
                .isFeature(true)
                .build();

        ResourceLocation module_libvlc = new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlc")).build();
        ResourceLocation module_libvlccore = new MutableModuleSingle(new ResourceLocation("vlc_modules", "libvlccore")).build();
        Request.ModuleRequestGraph.compileModuleLikeRegistry();
        Request.ModuleRequestGraph.buildAllRequests();
        System.out.println("Tests finished");
        return;
    }
}

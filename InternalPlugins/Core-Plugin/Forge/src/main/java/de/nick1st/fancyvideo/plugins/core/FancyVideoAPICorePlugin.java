package de.nick1st.fancyvideo.plugins.core;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
import nick1st.fancyvideo.Constants;

@Mod(FancyVideoAPICorePlugin.MODID)
public class FancyVideoAPICorePlugin {
    public static final String MODID = "fancyvideo_api_core_plugin";

    {
        new TestClass();
        new Constants();
        if(Minecraft.getInstance().player == null) {
            System.err.println("NULL");
        } else {
            System.err.println("PLAYER");
        }
    }
}

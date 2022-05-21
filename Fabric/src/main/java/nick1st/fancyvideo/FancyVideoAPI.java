package nick1st.fancyvideo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import nick1st.fancyvideo.config.SimpleConfig;

public class FancyVideoAPI implements ModInitializer {

    private static FancyVideoAPI instance;

    public FancyVideoAPI() {
        if (instance == null) {
            instance = this;
        } else {
            Constants.LOG.error("Called FancyVideo-API constructor a second time! This will cause serious problems!");
        }
    }


    // Common Class Holder
    private CommonMainClass commonClass;

    // Config Holder
    public SimpleConfig config;

    @Override
    public void onInitialize() {
        // Init Config
        config = new FancyVideoConfig();
        // Look in MixinMinecraft
        commonClass = new CommonMainClass(config);
    }

    public void firstRenderTick() {
        // Ensure this only runs on the client (Not sure if this is required)
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT && !Constants.renderTick) {
            commonClass.apiSetup();
            Constants.renderTick = true;
        }
    }

    public static FancyVideoAPI getInstance() {
        return instance;
    }
}

package nick1st.fancyvideo;

import nick1st.fancyvideo.config.SimpleConfig;
import nick1st.fancyvideo.platform.Services;

import java.io.File;
import java.util.Arrays;

public class FancyVideoConfig extends SimpleConfig {

    public FancyVideoConfig() {
        super(new File("config", "fancyvideo-api.cfg"));
        setProperty("dllVersion", String.valueOf(Constants.DLL_VERSION), "DO NOT MODIFY THIS! (Set it to -1 to regenerate your DLLs, but otherwise DO NOT TOUCH!)", ">= -1", s -> {
            try {
                if (Integer.parseInt(s) >= -1) {
                    return true;
                }
            } catch (NumberFormatException ignored) {
                // Ignored
            }
            return false;
        });
        if (Services.PLATFORM.getPlatformName().equals("Forge")) {
            setProperty("debugLog", String.valueOf(false), "Enable debug logging. Disables the ModLauncher log filter. This cause massive log spam! Only activate this when you're told to!", "true / false", s -> Arrays.asList("true", "false").contains(s));
        }
        setProperty("example", String.valueOf(false), "Activate the debug/showcase mode. Access it by pressing the Realms Button in Main Menu.", "true / false", s -> Arrays.asList("true", "false").contains(s));

        read();
        write();
    }
}

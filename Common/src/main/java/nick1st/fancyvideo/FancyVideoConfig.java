/*
 * This file is part of the FancyVideo-API.
 *
 * The FancyVideo-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The FancyVideo-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The FancyVideo-API uses VLCJ, Copyright 2009-2021 Caprica Software Limited,
 * licensed under the GNU General Public License.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You should have received a copy of the GNU General Public License
 * along with FancyVideo-API.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 Nick1st.
 */

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

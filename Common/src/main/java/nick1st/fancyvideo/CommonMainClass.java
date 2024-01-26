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

package nick1st.fancyvideo; //NOSONAR

import nick1st.fancyvideo.api.eventbus.EventException;
import nick1st.fancyvideo.api.eventbus.FancyVideoEventBus;
import nick1st.fancyvideo.api.plugins.Plugin;
import nick1st.fancyvideo.api.plugins.PluginLocator;
import nick1st.fancyvideo.api.plugins.PluginRegistry;
import nick1st.fancyvideo.config.SimpleConfig;
import nick1st.fancyvideo.example.NewExample;
import nick1st.fancyvideo.internal.Arch;
import org.apache.commons.lang3.SystemUtils;

import java.util.ServiceLoader;

public class CommonMainClass {

    public CommonMainClass(SimpleConfig config) {
        // Block of new 3.0.0
        loadPlugins();
        // End of block of new 3.0.0

        // Detect OS
        if (SystemUtils.IS_OS_LINUX) {
            Constants.OS = "linux";
        } else if (SystemUtils.IS_OS_MAC) {
            Constants.OS = "mac";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            Constants.OS = "windows";
        } else {
            Constants.OS = "unknown";
        }

        Constants.ARCH = Arch.getArch().toString();

        Constants.LOG.info("Running on OS: {} {}", Constants.OS, Constants.ARCH);

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        // Setup Example?
        if (config.getAsBool("example")) {
            // TODO
            try {
                FancyVideoEventBus.getInstance().registerEvent(new NewExample());
            } catch (EventException.EventRegistryException e) {
                throw new RuntimeException(e);
            } catch (EventException.UnauthorizedRegistryException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Loads all Plugins (implementations of {@link PluginLocator}) and registers them
     */
    private void loadPlugins() {
        ServiceLoader<PluginLocator> loader = ServiceLoader.load(PluginLocator.class);
        for (PluginLocator plugin : loader) {
            PluginRegistry.get().register(plugin.identifier(), new Plugin(plugin.getProvidedPlayers()));
        }
        PluginRegistry.get().freeze();
    }
}

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
            Constants.renderTick = true;
        }
    }

    public static FancyVideoAPI getInstance() {
        return instance;
    }
}

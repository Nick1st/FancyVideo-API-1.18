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

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.EventSubclassTransformer;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import nick1st.fancyvideo.api.eventbus.FancyVideoEventBus;
import nick1st.fancyvideo.config.SimpleConfig;
import nick1st.fancyvideo.example.DrawBackgroundEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.util.Arrays;

@Mod(Constants.MOD_ID)
public class FancyVideoAPI {

    // Config Holder
    private SimpleConfig config;

    // Common Class Holder
    private CommonMainClass commonClass;

    public FancyVideoAPI() {
        // Client only
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, ()-> new IExtensionPoint.DisplayTest(() -> "ANY", (a, b) -> true));
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            Constants.LOG.warn("## WARNING ## 'FancyVideo API' is a client mod and has no effect when loaded on a server!");
            return;
        }
//        Minecraft.getInstance().player.connection.commands.equals("Test"); //TODO REMOVE

        // Init Config
        config = new FancyVideoConfig();

        commonClass = new CommonMainClass(config);

        MinecraftForge.EVENT_BUS.addListener(this::firstRenderTick);
        MinecraftForge.EVENT_BUS.addListener(this::drawBackground);
    }

    public void firstRenderTick(TickEvent.RenderTickEvent event) {
        if (!Constants.renderTick) {
            Constants.renderTick = true;
        }
    }

    public void drawBackground(ScreenEvent.BackgroundRendered e) {
        FancyVideoEventBus.getInstance().runEvent(new DrawBackgroundEvent(e.getScreen(), e.getPoseStack()));
    }
}

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

package nick1st.fancyvideo.example;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.renderer.GameRenderer;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.eventbus.EventPhase;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.mediaPlayer.MediaPlayerBase;
import nick1st.fancyvideo.api.mediaPlayer.SimpleMediaPlayer;

public class APIExample {
    boolean init = false;
    DynamicResourceLocation resourceLocation;

    @FancyVideoEvent
    public void init(PlayerRegistryEvent.AddPlayerEvent event) {
        Constants.LOG.info("Setting up example media player");
        resourceLocation = new DynamicResourceLocation(Constants.MOD_ID, "example");
        event.handler().registerPlayerOnFreeResLoc(resourceLocation, SimpleMediaPlayer.class);
        if (event.handler().getMediaPlayer(resourceLocation).providesAPI()) {
            event.handler().getMediaPlayer(resourceLocation).api().media().prepare("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
            event.handler().getMediaPlayer(resourceLocation).api().audio().setVolume(200);
        } else {
            Constants.LOG.warn("Example running in NO_LIBRARY_MODE");
        }
    }

    @FancyVideoEvent
    public void drawBackground(DrawBackgroundEvent event) {
        if (event.screen instanceof OptionsScreen && resourceLocation != null &&
        MediaPlayerHandler.getInstance().getMediaPlayer(resourceLocation) instanceof MediaPlayerBase mediaPlayer) {
            if (MediaPlayerHandler.getInstance().getMediaPlayer(resourceLocation).providesAPI()) {
                if (!init) {
                    mediaPlayer.api().controls().play();
                    init = true;
                }
                // Generic Render Code for Screens
                int width = Minecraft.getInstance().screen.width;
                int height = Minecraft.getInstance().screen.height;

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, mediaPlayer.renderToResourceLocation());

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                GuiComponent.blit(event.poseStack, 0, 0, 0.0F, 0.0F, width, height, width, height);
                RenderSystem.disableBlend();
            } else {
                // Generic Render Code for Screens
                int width = Minecraft.getInstance().screen.width;
                int height = Minecraft.getInstance().screen.height;

                int width2;

                if (width <= height) {
                    width2 = width / 3;
                } else {
                    width2 = height / 2;
                }

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, new DynamicResourceLocation(Constants.MOD_ID, "fallback"));

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                GuiComponent.blit(event.poseStack, 0, 0, 0.0F, 0.0F, width, height, width2, width2);
                RenderSystem.disableBlend();
            }
        }
    }

    @FancyVideoEvent(phase = EventPhase.PRE)
    public void removePlayer(PlayerRegistryEvent.RemovePlayerEvent event) {
        if (event.resourceLocation == resourceLocation) {
            resourceLocation = null;
        }
    }
}

package nick1st.fancyvideo.example;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.realms.RealmsScreen;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.eventbus.FancyVideoEvent;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.api.player.MediaPlayer;
import nick1st.fancyvideo.api.player.PlayerFactoryHelper;
import nick1st.fancyvideo.api.plugins.PluginRegistry;

public class NewExample {

    MediaPlayer player = null;

    @FancyVideoEvent
    @SuppressWarnings("unused")
    public void init(PlayerRegistryEvent.AddPlayerEvent event) {
        Constants.LOG.info("Setting up new example media player");
    }

    @FancyVideoEvent
    @SuppressWarnings("unused")
    public void drawBackground(DrawBackgroundEvent event) {
        if (event.getScreen() instanceof RealmsScreen ) {
            if (player == null) {
                PlayerFactoryHelper initStateHelper = PluginRegistry.get().getPlayerForQuery(null);
                if (initStateHelper == null) {
                    throw new RuntimeException(); // TODO Better exception, checks etc
                }
                player = initStateHelper.create();
            }

            if (player == null) {
                renderFallback(event.getPoseStack());
            } else {
                int width = Minecraft.getInstance().screen.width;
                int height = Minecraft.getInstance().screen.height;

                RenderSystem.setShader(GameRenderer::getPositionTexShader);
                RenderSystem.setShaderTexture(0, null); //mediaPlayer.renderToResourceLocation()

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                GuiComponent.blit(event.getPoseStack(), 0, 0, 0.0F, 0.0F, width, height, width, height);
                RenderSystem.disableBlend();
            }
        }
    }

    public void renderFallback(PoseStack stack) {
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
        GuiComponent.blit(stack, 0, 0, 0.0F, 0.0F, width, height, width2, width2);
        RenderSystem.disableBlend();
    }
}

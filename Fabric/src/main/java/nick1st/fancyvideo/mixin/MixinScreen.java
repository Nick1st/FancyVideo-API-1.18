package nick1st.fancyvideo.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import nick1st.fancyvideo.FancyVideoAPI;
import nick1st.fancyvideo.api.eventbus.FancyVideoEventBus;
import nick1st.fancyvideo.example.DrawBackgroundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Screen.class)
public abstract class MixinScreen {
    //BackgroundDrawnEvent
    @Inject(at = @At(value = "TAIL"), method = "renderBackground(Lcom/mojang/blaze3d/vertex/PoseStack;I)V")
    private void onBackgroundDrawn(PoseStack matrix, int vOffset, CallbackInfo info) {
        FancyVideoEventBus.getInstance().runEvent(new DrawBackgroundEvent((Screen)((Object)this), matrix));
    }

    //BackgroundDrawnEvent
    @Inject(at = @At(value = "TAIL"), method = "renderDirtBackground")
    private void onBackgroundTextureDrawn(int vOffset, CallbackInfo info) {
        FancyVideoEventBus.getInstance().runEvent(new DrawBackgroundEvent((Screen)((Object)this), new PoseStack()));
    }
}

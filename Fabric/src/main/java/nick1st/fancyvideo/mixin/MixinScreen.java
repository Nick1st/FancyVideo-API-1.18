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

package nick1st.fancyvideo.mixin; //NOSONAR

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
@SuppressWarnings("unused")
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

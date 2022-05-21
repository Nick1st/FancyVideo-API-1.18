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

package nick1st.fancyvideo.example; //NOSONAR

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import nick1st.fancyvideo.api.eventbus.event.Event;

/**
 * Event fired after the Background of a Screen is drawn.
 * @since 0.2.0.0
 */
@SuppressWarnings("unused")
public class DrawBackgroundEvent extends Event {
    private final Screen screen;
    private PoseStack poseStack;

    public DrawBackgroundEvent(Screen screen, PoseStack matrixStack) {
        this.screen = screen;
        this.poseStack = matrixStack;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    @Override
    public void onFinished() {
        // I don't need any cleanup
    }

    public Screen getScreen() {
        return screen;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }

    public void setPoseStack(PoseStack poseStack) {
        this.poseStack = poseStack;
    }
}

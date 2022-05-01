package nick1st.fancyvideo.example;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import nick1st.fancyvideo.api.eventbus.event.Event;

public class DrawBackgroundEvent extends Event {
    public Screen screen;
    public PoseStack poseStack;

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

    }
}

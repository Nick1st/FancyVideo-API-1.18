package nick1st.fancyvideo;

import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import nick1st.fancyvideo.api.EmptyMediaPlayer;
import nick1st.fancyvideo.example.MatrixStackRenderTest;

public class Example {

    // Example
    public MatrixStackRenderTest matrixRenderTest;

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event) { //TODO Move to common part
        if (!Constants.renderTick) {
            EmptyMediaPlayer.getInstance().setUp();
            Constants.LOG.info("Tick");
            matrixRenderTest = new MatrixStackRenderTest();
            matrixRenderTest.init();
            MinecraftForge.EVENT_BUS.addListener(this::drawBackground);
            Constants.renderTick = true;
        }
    }

    @SubscribeEvent
    public void drawBackground(ScreenEvent.BackgroundDrawnEvent e) {
        matrixRenderTest.drawBackground(e.getScreen(), e.getPoseStack());
    }
}

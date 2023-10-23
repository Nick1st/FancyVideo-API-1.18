package nick1st.fancyvideo.api.mediaPlayer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;
import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Pipeline;
import org.freedesktop.gstreamer.Sample;
import org.freedesktop.gstreamer.elements.AppSink;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.concurrent.Semaphore;

import static org.freedesktop.gstreamer.FlowReturn.OK;

public class GstPlayer extends MediaPlayerBase {
    // Frame Holders
    protected final Semaphore semaphore = new Semaphore(1, true);
    // MediaPlayer
    protected Pipeline pipeline;
    protected IntegerBuffer2D videoFrame = new IntegerBuffer2D(1, 1);

    public GstPlayer(DynamicResourceLocation resourceLocation) {
        super(resourceLocation);

        pipeline = (Pipeline) Gst.parseLaunch("videotestsrc ! video/x-raw,width=1280,height=720,format=RGBA ! queue ! appsink name=appsink");
        AppSink sink = (AppSink) pipeline.getElementByName("appsink");
        sink.set("emit-signals", true);
        sink.connect((AppSink.NEW_SAMPLE) elem -> {
            Sample s = elem.pullSample();
            Buffer b = s.getBuffer();
            s.dispose();
            ByteBuffer bb = b.map(false);

            IntBuffer ib = bb.asIntBuffer();
            setIntBuffer(new IntegerBuffer2D(1280, ib));

            b.unmap();
            b.dispose();
            //s.dispose();
            return OK;
        });
        pipeline.play();
    }

    @Override
    public EmbeddedMediaPlayer api() {
        return null;
    }

    @Override
    public void markToRemove() {
        super.markToRemove();
        MediaPlayerHandler.getInstance().flagPlayerRemoval(dynamicResourceLocation);
    }

    @Override
    public void cleanup() {
        if (Constants.LOG.isDebugEnabled()) {
            Constants.LOG.debug("Removing Player '{}'", dynamicResourceLocation.toWorkingString());
        }
        if (providesAPI()) {
            pipeline.dispose();
        }
    }

    @Override
    public int[] getIntFrame() {
        try {
            semaphore.acquire();
            IntegerBuffer2D temp = new IntegerBuffer2D(videoFrame);
            semaphore.release();
            return temp.getArray();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return new int[0];
    }

    @Override
    public int getWidth() {
        int width = 0;
        try {
            semaphore.acquire();
            width = videoFrame.getWidth();
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return width;
    }

    @Override
    public void setIntBuffer(IntegerBuffer2D in) {
        try {
            semaphore.acquire();
            videoFrame = new IntegerBuffer2D(in);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public IntegerBuffer2D getIntBuffer() {
        try {
            semaphore.acquire();
            IntegerBuffer2D currentFrame = new IntegerBuffer2D(videoFrame);
            semaphore.release();
            return currentFrame;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return super.getIntBuffer();
    }

    @Override
    public ResourceLocation renderToResourceLocation() {
        IntegerBuffer2D buffer2D = getIntBuffer();
        int width = buffer2D.getWidth();
        if (width == 0) {
            return dynamicResourceLocation;
        }
        image = new NativeImage(width, buffer2D.getHeight(), true);
        for (int i = 0; i < buffer2D.getHeight(); i++) {
            for (int j = 0; j < width; j++) {
                image.setPixelRGBA(j, i, buffer2D.get(j, i));
            }
        }
        dynamicTexture.setPixels(image);
        return dynamicResourceLocation;
    }

    @Override
    public boolean providesAPI() {
        return pipeline != null;
    }
}

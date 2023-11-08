package nick1st.fancyvideo.api.mediaPlayer;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.elements.PlayBin;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.net.URI;
import java.net.URISyntaxException;
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

    private static PlayBin testBin;

    public GstPlayer(DynamicResourceLocation resourceLocation) {
        super(resourceLocation);

        String[] args = new String[]{ "--gst-enable-gst-debug", "--gst-debug=gldisplay:3" };
        Gst.init(Version.of(1, 10), "BasicPipeline", args);
        testBin = new PlayBin("testBin");

        Bin bin3 = new Bin("inputDistributorBin"); // Step 1: Create a Bin
        Element tee = ElementFactory.make("tee", "t"); // Step 2: Create your Elements
        Element queue1 = ElementFactory.make("queue", "queue1");
        Element queue2 = ElementFactory.make("queue", "queue2");
        Element queue3 = ElementFactory.make("queue", "queue3");
        AppSink appSink1 = new AppSink("appsink1");
        appSink1.set("emit-signals", true);

        // GLTest

        // GLTest End

//        pipeline = (Pipeline) Gst.parseLaunch("videotestsrc ! video/x-raw,width=1280,height=720,format=RGBA ! queue ! appsink name=appsink");
//        AppSink sink = (AppSink) pipeline.getElementByName("appsink");
//        sink.set("emit-signals", true);

        appSink1.connect((AppSink.NEW_SAMPLE) elem -> {
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
//        pipeline.play();

        bin3.add(tee); // Step 3: Add your Elements to your Bin
        //bin3.add(appSink1);
        bin3.addMany(queue1, queue2, queue3);
        //tee.addPad(new Pad("src_0", PadDirection.SRC)); // Step 4: Manually add Pads if required. Not required for smart-link it seems
        //tee.getSrcPads().get(0).link(appSink1.getSinkPads().get(0)); // Step 5: Link your elements by either linking their pads or using smart-link on the elements
        //tee.link(appSink1);
        tee.link(queue1);
        tee.link(queue2);
        tee.link(queue3);
        bin3.addPad(new GhostPad(null, tee.getSinkPads().get(0))); // Step 5: Add a GhostPad "publishing" the SrcPads and SinkPads of the first Element in the Bin pipe
        bin3.addPad(new GhostPad(null, bin3.getElementByName("queue3").getSrcPads().get(0)));
        bin3.addPad(new GhostPad(null, bin3.getElementByName("queue2").getSrcPads().get(0)));
        bin3.addPad(new GhostPad(null, bin3.getElementByName("queue1").getSrcPads().get(0)));

        // Fake Pipeline to debug pad connections
        Bin fakePipe = new Bin("fakePipeline");
        Element videoconvert = ElementFactory.make("videoconvert", "videoconvert");
        fakePipe.add(videoconvert);
        fakePipe.add(bin3);
        Element autovideosink1 = ElementFactory.make("ximagesink", "autovideosink1");
        Element autovideosink2 = ElementFactory.make("ximagesink", "autovideosink2");
        Element autovideosink3 = ElementFactory.make("ximagesink", "autovideosink3");
        fakePipe.addMany(autovideosink1, autovideosink2, appSink1);
        bin3.link(autovideosink1);
        bin3.link(autovideosink2);
        bin3.link(appSink1);
        videoconvert.link(bin3);
        fakePipe.addPad(new GhostPad("sink", fakePipe.getElementByName("videoconvert").getSinkPads().get(0))); // A pad that is used as a sink for a playbin must be named "sink"

        testBin.setVideoSink(fakePipe);

        try {
            testBin.setURI(new URI("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        System.out.println(testBin.getElements().size());

        testBin.play();
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
        return testBin != null || pipeline != null;
    }
}

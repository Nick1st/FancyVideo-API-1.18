package de.nick1st.fancyvideo.backends.gst;

import nick1st.fancyvideo.api.helpers.MediaSource;
import nick1st.fancyvideo.api.helpers.capabilities.DefaultCapabilities;
import nick1st.fancyvideo.api.helpers.exceptions.player.MethodUnsupportedPlayerStateException;
import nick1st.fancyvideo.api.helpers.exceptions.player.MissingCapabilityException;
import nick1st.fancyvideo.api.helpers.formats.ByteBufferVideoFormat;
import nick1st.fancyvideo.api.helpers.formats.VideoFormatNew;
import nick1st.fancyvideo.api.player.MediaPlayer;
import nick1st.fancyvideo.api.player.PlayerFactoryHelper;
import nick1st.fancyvideo.api.player.PlayerState;
import nick1st.fancyvideo.api.player.PlayerSupportsVideo;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.elements.PlayBin;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static org.freedesktop.gstreamer.FlowReturn.OK;

public class GstVideoPlayer extends MediaPlayer implements PlayerSupportsVideo { // TODO Work on this next time opened NO STATICS!!!

    public static AtomicBoolean gstInit = new AtomicBoolean(false);

    private PlayBin playBin;

    private VideoFormatNew<?> videoSinkFormat;

    public GstVideoPlayer(PlayerFactoryHelper initInfo, boolean isQuery) {
        super(initInfo, isQuery);
        if (!isQuery) {
            if (!gstInit.get()) {
                String[] args = new String[]{ "--gst-enable-gst-debug", "--gst-debug=gldisplay:3" };
                Gst.init(Version.of(1, 10), "FancyVideoAPI-GstBackend", args);
                gstInit.set(true);
            }

            playBin = new PlayBin("FancyVideoAPIGstBackendMainPlayBinContainer::" + initInfo.getPlayerResourceLocation().toString());

            Bin inputDistributorBin = new Bin("inputDistributorBin"); // Step 1: Create a Bin
            Element tee = ElementFactory.make("tee", "t"); // Step 2: Create your Elements
            Element queue1 = ElementFactory.make("queue", "queue1");
            Element queue2 = ElementFactory.make("queue", "queue2");

            ByteBufferVideoFormat format = ((ByteBufferVideoFormat) videoSinkFormat); // TODO Do NOT hardcode this

            AppSink videoAppSink = new AppSink("appsink");
            videoAppSink.set("emit-signals", true);
            videoAppSink.connect((AppSink.NEW_SAMPLE) elem -> {
                Sample s = elem.pullSample();
                Buffer b = s.getBuffer();
                s.dispose();
                ByteBuffer bb = b.map(false);

                Consumer<ByteBuffer> byteBufferWriteCallback = (ByteBuffer byteBuffer) -> {
                    byteBuffer.put(bb);
                }; // TODO This is so terribly wrong
                format.setBufferWriteCallback(byteBufferWriteCallback);

                format.setBuffer();

                b.unmap();
                b.dispose();
                return OK;
            });

            inputDistributorBin.add(tee); // Step 3: Add your Elements to your Bin
            inputDistributorBin.addMany(queue1, queue2);
            //tee.addPad(new Pad("src_0", PadDirection.SRC)); // Step 4: Manually add Pads if required. Not required for smart-link it seems
            //tee.getSrcPads().get(0).link(appSink1.getSinkPads().get(0)); // Step 5: Link your elements by either linking their pads or using smart-link on the elements
            tee.link(queue1);
            tee.link(queue2);
            inputDistributorBin.addPad(new GhostPad(null, tee.getSinkPads().get(0))); // Step 5: Add a GhostPad "publishing" the SrcPads and SinkPads of the first Element in the Bin pipe
            inputDistributorBin.addPad(new GhostPad(null, inputDistributorBin.getElementByName("queue2").getSrcPads().get(0)));
            inputDistributorBin.addPad(new GhostPad(null, inputDistributorBin.getElementByName("queue1").getSrcPads().get(0)));

            // Fake Pipeline to debug pad connections
            Bin fakePipe = new Bin("fakePipeline");
            Element videoconvert = ElementFactory.make("videoconvert", "videoconvert");
            fakePipe.add(videoconvert);
            fakePipe.add(inputDistributorBin);
            Element autovideosink1 = ElementFactory.make("ximagesink", "autovideosink1");
            fakePipe.addMany(autovideosink1, videoAppSink);
            inputDistributorBin.link(autovideosink1);
            inputDistributorBin.link(videoAppSink);
            videoconvert.link(inputDistributorBin);
            fakePipe.addPad(new GhostPad("sink", fakePipe.getElementByName("videoconvert").getSinkPads().get(0))); // A pad that is used as a sink for a playbin must be named "sink"

            playBin.setVideoSink(fakePipe);
        }
    }

    /**
     * Sets the media source this player should play from.
     *
     * @param source the mediaSource this player should play from
     * @since 3.0.0
     */
    @Override
    public void setMediaSource(MediaSource source) {
        playBin.setURI(source.mediaURI);
        state = PlayerState.INITIALIZED;
    }

    @Override
    protected void _play() {
        state = PlayerState.PLAYING;
        playBin.play();
    }

    /**
     * Pauses playback
     *
     * @throws MissingCapabilityException            if the player does not have the capability required to invoke this method.
     * @throws MethodUnsupportedPlayerStateException if the player was not in state {@link PlayerState#PLAYING}
     * @since 3.0.0
     */
    @Override
    public void pause() {
        super.pause();
        state = PlayerState.PAUSED;
        if (playBin.pause() == StateChangeReturn.FAILURE) {
            throw new RuntimeException(); // TODO Exception
        }
    }

    /**
     * Resumes playback
     *
     * @throws MissingCapabilityException            if the player does not have the capability required to invoke this method.
     * @throws MethodUnsupportedPlayerStateException if the player was not in state {@link PlayerState#PLAYING}
     * @since 3.0.0
     */
    @Override
    public void resume() {
        super.resume();
        state = PlayerState.PLAYING;
        if (playBin.play() == StateChangeReturn.FAILURE) {
            throw new RuntimeException(); // TODO Exception
        }
    }

    /**
     * Stops playback.
     *
     * @since 3.0.0
     */
    @Override
    public void stop() {
        state = PlayerState.STOPPED;
        if (playBin.stop() == StateChangeReturn.FAILURE) {
            throw new RuntimeException(); // TODO Exception
        }
    }

    /**
     * @param capability The capability this implementation might have / might not have
     * @return true if the capability is there, false otherwise
     * @apiNote The result of this method for a specific input can change during the players' lifetime, however
     * <b>ONLY</b> from <b>false to true</b>.
     * @since 3.0.0
     */
    @Override
    public boolean hasCapability(String capability) {
        switch (capability) {
            case DefaultCapabilities.PAUSE -> {
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    /**
     * Called if player is in an invalid, non-recoverable state. <br>
     *
     * @apiNote Implementations should override this method.
     * @since 3.0.0
     */
    @Override
    public void onInvalid() {
        super.onInvalid();
    }

    /**
     * Releases all resources held by this player.
     *
     * @since 3.0.0
     */
    @Override
    public void close() {

    }

    /**
     * @param videoFormat the video format requested
     * @return true of the player can provide this format, false otherwise
     * @since 3.0.0
     */
    @Override
    public boolean supportsVideoSinkFormat(VideoFormatNew<?> videoFormat) {
        if (videoFormat instanceof ByteBufferVideoFormat byteBufferVideoFormat) {
            // TODO Maybe: Better, more detailed implementation
            return true;
        }
        return false;
    }

    /**
     * Sets the format the player should sink as. Should be called in {@link PlayerState#INITIALIZED}
     *
     * @param sinkFormat The (supported) format this player will sink as
     * @apiNote Remember that the format set here is a <b>BINDING</b> contract
     * @since 3.0.0
     */
    @Override
    public void setVideoSinkFormat(VideoFormatNew<?> sinkFormat) {
        // TODO Better implementation
        videoSinkFormat = sinkFormat;
    }
}

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

package nick1st.fancyvideo.api;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.internal.AdvancedFrame;
import nick1st.fancyvideo.api.internal.MediaPlayerCallback;
import nick1st.fancyvideo.api.internal.SelfCleaningDynamicTexture;
import nick1st.fancyvideo.api.internal.utils.BufferToMatrixStack;
import uk.co.caprica.vlcj.player.component.CallbackMediaListPlayerComponent;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormat;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.BufferFormatCallbackAdapter;
import uk.co.caprica.vlcj.player.embedded.videosurface.callback.format.RV32BufferFormat;

import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

/**
 * Main class of the MediaPlayerAPI.
 * Call {@link #getNew()} to create a new media player. <br>
 * Use {@link MediaPlayers#getPlayer(int)} to get the reference of your player. <br>
 * Please call {@link #destroy()} when you don't need it anymore. <br>
 * <b>Your player may get delete on certain event calls (e.g. {link ShutdownEvent}). You need to be aware of this. </b>
 * <b>Use {@link MediaPlayers#isValid(int)} to check if the player can be used.</b>
 */
public class MediaPlayer {
    protected final MediaPlayerCallback callback = new MediaPlayerCallback(0, this);
    protected final int id;
    protected final Semaphore semaphore = new Semaphore(1, true);
    // Important stuff
    protected CallbackMediaPlayerComponent mediaPlayerComponent;
    // The last rendered frame is stored here
    protected AdvancedFrame videoStream = new AdvancedFrame(new int[0], 0);
    // Image
    protected NativeImage image = new NativeImage(1, 1, true);
    protected final SelfCleaningDynamicTexture dyTex = new SelfCleaningDynamicTexture(image);
    protected ResourceLocation loc;

    MediaPlayer() {
        mediaPlayerComponent = new CallbackMediaListPlayerComponent(MediaPlayers.getInstance().factory, null, null, true, null, callback, new DefaultBufferFormatCallback(), null);
        id = MediaPlayers.addPlayer(this);
        init();
    }

    /**
     * Creates a new MediaPlayer for you. Call {@link MediaPlayers#getPlayer(int)} to get your reference
     * Please call {@link #destroy()} when you don't need it anymore
     *
     * @return ID of the new MediaPlayer (keep it, it's important!)
     */
    public static int getNew() {
        return new MediaPlayer().id;
    }

    /**
     * Init an empty frame (Hex: 0x000000; Alpha: 0x00)
     */
    public void init() {
        image = new NativeImage(1, 1, true);
        image.setPixelRGBA(0, 0, 0);
        dyTex.setPixels(image);
        if (loc == null) {
            loc = Minecraft.getInstance().getTextureManager().register("video_texture" + id, dyTex);
        }
    }

    void destroy() {
        Constants.LOG.info("Destroyed");
        mediaPlayerComponent.mediaPlayer().controls().stop();
        mediaPlayerComponent.release();
    }

    public void play(String mrl, String... options) {
        mediaPlayerComponent.mediaPlayer().media().play(mrl, options);
    }

    /**
     * @param percentage Reaches 0 - 200
     */
    public void volume(int percentage) {
        mediaPlayerComponent.mediaPlayer().audio().setVolume(percentage);
    }

    public void mute() {
        mediaPlayerComponent.mediaPlayer().audio().mute();
    }

    public void prepare(String mrl, String... options) {
        mediaPlayerComponent.mediaPlayer().media().prepare(mrl, options);
    }

    public void playPrepared() {
        mediaPlayerComponent.mediaPlayer().controls().play();
    }

    public void preparePaused(String mrl, String... options) {
        mediaPlayerComponent.mediaPlayer().media().startPaused(mrl, options);
    }

    public void pause() {
        mediaPlayerComponent.mediaPlayer().controls().pause();
    }

    public int[] getFrame() {
        try {
            semaphore.acquire();
            int[] currentFrame = new AdvancedFrame(videoStream).frame;
            semaphore.release();
            return currentFrame;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return new int[0];
    }

    public void setFrame(AdvancedFrame in) {
        try {
            semaphore.acquire();
            videoStream = new AdvancedFrame(in);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    public AdvancedFrame getFrameAdvanced() {
        try {
            semaphore.acquire();
            AdvancedFrame currentFrame = new AdvancedFrame(videoStream);
            semaphore.release();
            return currentFrame;
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        return new AdvancedFrame(new int[0], 0);
    }

    /**
     * @return This returns the true {@link CallbackMediaListPlayerComponent}, allowing you to use (nearly) all functions of libvlc.
     * Only use when you know what you're doing.
     */
    public CallbackMediaPlayerComponent getTrueMediaPlayer() {
        return mediaPlayerComponent;
    }

    public PoseStack render(PoseStack matrixStack, int x, int y) {
        AdvancedFrame frameAdvanced = getFrameAdvanced();
        int[] frame = frameAdvanced.frame;
        int width = frameAdvanced.width;
        BufferToMatrixStack bufferStack = new BufferToMatrixStack(matrixStack);
        IntStream.range(0, frame.length).forEach(index -> bufferStack.set(index % width + x, index / width + y, frame[index]));
        bufferStack.finishDrawing();
        return matrixStack;
    }

    /**
     * Renders the current frame to a {@link ResourceLocation} for further use.
     *
     * @return The {@link ResourceLocation} rendered to.
     */
    public ResourceLocation renderImage() {
        AdvancedFrame frameAdvanced = getFrameAdvanced();
        int[] frame = frameAdvanced.frame;
        int width = frameAdvanced.width;
        if (width == 0) {
            return loc;
        }
        image = new NativeImage(width, frame.length / width, true);
        IntStream.range(0, frame.length).forEach(index -> {
            int x = index % width;
            int y = index / width;

            int color = frame[index];
            color <<= 8;
            color |= 0xFF;
            color = Integer.reverseBytes(color);

            image.setPixelRGBA(x, y, color);
        });
        dyTex.setPixels(image);
        return loc;
    }

    /**
     * Binds the current frame for further use.
     */
    public void bindFrame() {
        Minecraft.getInstance().getTextureManager().bindForSetup(renderImage());
    }


    /**
     * Default implementation of a buffer format callback that returns a buffer format suitable for rendering RGB frames
     */
    private class DefaultBufferFormatCallback extends BufferFormatCallbackAdapter {
        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            Constants.LOG.info("Dimensions of player {}: {} | {}", id, sourceWidth, sourceHeight);
            callback.setBuffer(new AdvancedFrame(new int[sourceWidth * sourceHeight], sourceWidth));
            return new RV32BufferFormat(sourceWidth, sourceHeight);
        }
    }
}

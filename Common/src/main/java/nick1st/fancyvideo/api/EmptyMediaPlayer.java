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

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.internal.AdvancedFrame;
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent;

public final class EmptyMediaPlayer extends MediaPlayer { //TODO Make this the root class, in order to get NO_LIBRARY mode working

    private static final AdvancedFrame emptyAdvancedFrame = new AdvancedFrame(new int[0], 0);
    private static EmptyMediaPlayer instance;

    private EmptyMediaPlayer() {
        super();
        this.destroy();
        mediaPlayerComponent = null;
    }

    public static synchronized EmptyMediaPlayer getInstance() {
        if (EmptyMediaPlayer.instance == null) {
            EmptyMediaPlayer.instance = new EmptyMediaPlayer();
        }
        return EmptyMediaPlayer.instance;
    }

    public void setUp() {
        super.init();
    }

    @Override
    public void destroy() {
        if (this.mediaPlayerComponent != null) {
            super.destroy();
        } else {
            Constants.LOG.info("Destroy called on EmptyMediaPlayer");
        }
    }

    @Override
    public void init() {
        // Void Callback
    }

    @Override
    public void play(String mrl, String... options) {
        // Void Callback
    }

    @Override
    public void volume(int percentage) {
        // Void Callback
    }

    @Override
    public void mute() {
        // Void Callback
    }

    @Override
    public void prepare(String mrl, String... options) {
        // Void Callback
    }

    @Override
    public void playPrepared() {
        // Void Callback
    }

    @Override
    public void preparePaused(String mrl, String... options) {
        // Void Callback
    }

    @Override
    public void pause() {
        // Void Callback
    }

    @Override
    public int[] getFrame() {
        return new int[0];
    }

    @Override
    public AdvancedFrame getFrameAdvanced() {
        return emptyAdvancedFrame;
    }

    @Override
    public CallbackMediaPlayerComponent getTrueMediaPlayer() throws NullPointerException {
        throw new NullPointerException("Running on EmptyMediaPlayer");
    }

    @Override
    public PoseStack render(PoseStack matrixStack, int x, int y) {
        return matrixStack;
    }

    @Override
    public ResourceLocation renderImage() {
        return this.loc;
    }

    @Override
    public void bindFrame() {
        Minecraft.getInstance().getTextureManager().bindForSetup(new ResourceLocation("minecraft", "dynamic/video_texture0_1"));
    }

}

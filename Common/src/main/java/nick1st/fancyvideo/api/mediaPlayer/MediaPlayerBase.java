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

package nick1st.fancyvideo.api.mediaPlayer; //NOSONAR

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.DynamicResourceLocation;
import nick1st.fancyvideo.api.internal.MediaPlayerCallback;
import nick1st.fancyvideo.api.internal.SelfCleaningDynamicTexture;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * For most custom implementation this should be enough to override.
 *
 * @see nick1st.fancyvideo.api.mediaPlayer.AbstractMediaPlayer
 * @since 0.2.0.0
 */
@SuppressWarnings("unused")
public class MediaPlayerBase extends AbstractMediaPlayer {

    /**
     * Last available Frame, stored as a {@link DynamicResourceLocation}
     **/
    public final DynamicResourceLocation dynamicResourceLocation;
    // MediaPlayerCallback
    public final MediaPlayerCallback callback = new MediaPlayerCallback(0, this);
    /**
     * Last available Frame, stored as a {@link NativeImage}
     **/
    protected NativeImage image = new NativeImage(1, 1, true); //TODO This redundant initializer seems to be important?
    /**
     * Last available Frame, stored as a {@link SelfCleaningDynamicTexture}
     **/
    protected SelfCleaningDynamicTexture dynamicTexture = new SelfCleaningDynamicTexture(image);

    public MediaPlayerBase(DynamicResourceLocation resourceLocation) {
        image = new NativeImage(1, 1, true);
        image.setPixelRGBA(0, 0, 0);
        dynamicTexture.setPixels(image);
        dynamicResourceLocation = resourceLocation;
        Minecraft.getInstance().getTextureManager().register(resourceLocation.toWorkingString().replace(':', '.'), dynamicTexture);
        Constants.LOG.debug("TextureLocation is '{}'", dynamicResourceLocation);
    }

    /**
     * Template methode. <br>
     * Overrides should always return a valid {@link EmbeddedMediaPlayer}
     *
     * @return null
     * @since 0.2.0.0
     */
    @Override
    public EmbeddedMediaPlayer api() {
        return null;
    }

    @Override
    public void markToRemove() {
        // Template methode.
    }

    @Override
    public void cleanup() {
        // Template methode.
    }

    /**
     * Template methode. <br>
     * This returns the current video frame as an int[] suitable for drawing to a {@link com.mojang.blaze3d.vertex.PoseStack}.
     * Use {@link #getWidth()} to get the buffer width.
     *
     * @since 0.2.0.0
     */
    public int[] getIntFrame() {
        return new int[0];
    }

    /**
     * Template methode. <br>
     * This returns the width of the current video frame.
     *
     * @since 0.2.0.0
     */
    public int getWidth() {
        return 0; //NOSONAR
    }

    /**
     * Template methode. <br>
     * Invoked by the callback to set a new frame. Should only be used by the callback, or if you want to inject custom frames.
     *
     * @since 0.2.0.0
     */
    public void setIntBuffer(IntegerBuffer2D in) {
        // Template methode.
    }

    /**
     * Template methode. <br>
     *
     * @since 0.2.0.0
     */
    public IntegerBuffer2D getIntBuffer() {
        return new IntegerBuffer2D(1, 1);
    }

    /**
     * Template methode. <br>
     * Renders the current frame to a {@link ResourceLocation} for further use.
     *
     * @return The {@link ResourceLocation} rendered to.
     */
    public ResourceLocation renderToResourceLocation() {
        return dynamicResourceLocation;
    }
}

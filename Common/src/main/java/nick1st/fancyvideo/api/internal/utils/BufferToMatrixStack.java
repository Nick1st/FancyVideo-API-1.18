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

package nick1st.fancyvideo.api.internal.utils; //NOSONAR

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Provides some legacy code for the (currently) broken renderToMatrixStack method.
 *
 * @since 0.0.0.0
 */
@SuppressWarnings("unused")
public class BufferToMatrixStack {
    public final Matrix4f matrix4f;
    public final BufferBuilder bb;

    public BufferToMatrixStack(PoseStack matrix) {
        matrix4f = matrix.last().pose();
        bb = Tesselator.getInstance().getBuilder();
        RenderSystem.disableTexture();
        // Required for transparency
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR); //TODO Validate that VertexFormat.Mode.QUADS matches vertex mode 7
    }

    public BufferToMatrixStack(PoseStack matrix, Tesselator tesselator) {
        matrix4f = matrix.last().pose();
        bb = tesselator.getBuilder();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }

    public static int[] extractBytes(String imageName) throws IOException {
        // open image
        File imgPath = new File(imageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        int[] image = new int[bufferedImage.getHeight() * bufferedImage.getWidth()];
        int k = 0;
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                image[k] = bufferedImage.getRGB(j, i);
                k++;
            }
        }
        return image;
    }

    public BufferToMatrixStack set(float minX, float minY, float maxX, float maxY, int color, float opacity) {
        if (minX < maxX) {
            float i = minX;
            minX = maxX;
            maxX = i;
        }
        if (minY < maxY) {
            float j = minY;
            minY = maxY;
            maxY = j;
        }

        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        float a = (color >> 24 & 255) / 255.0F;

        a = a * opacity;

        bb.vertex(matrix4f, minX, maxY, 0.0F).color(r, g, b, a).endVertex();
        bb.vertex(matrix4f, maxX, maxY, 0.0F).color(r, g, b, a).endVertex();
        bb.vertex(matrix4f, maxX, minY, 0.0F).color(r, g, b, a).endVertex();
        bb.vertex(matrix4f, minX, minY, 0.0F).color(r, g, b, a).endVertex();

        return this;
    }

    public void set(int x, int y, int color) {

        // Create color
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;

        bb.vertex(matrix4f, x, y + 1.0F, 0.0F).color(r, g, b, 1F).endVertex();
        bb.vertex(matrix4f, x + 1.0F, y + 1.0F, 0.0F).color(r, g, b, 1F).endVertex();
        bb.vertex(matrix4f, x + 1.0F, y, 0.0F).color(r, g, b, 1F).endVertex();
        bb.vertex(matrix4f, x, y, 0.0F).color(r, g, b, 1F).endVertex();
    }

    public void finishDrawing() {
        bb.end();
        BufferUploader.end(bb);
        RenderSystem.enableTexture();
        // Required for transparency
        RenderSystem.disableBlend();
    }

    public void finishDrawingTest() {
        bb.end();
        BufferUploader.end(bb);
    }
}

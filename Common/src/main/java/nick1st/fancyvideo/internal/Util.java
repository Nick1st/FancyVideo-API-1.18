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

package nick1st.fancyvideo.internal; //NOSONAR

import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Util {

    private Util() {}

    public static int[] extractBytes(String imageName, ClassLoader loader) {
        try (InputStream in = loader.getResourceAsStream(imageName)){
            if (in == null) {
                Constants.LOG.info("FindMe", new IOException("Resource not found."));
                throw new IOException("Resource not found.");
            }
            BufferedImage bufferedImage = ImageIO.read(in);

            int[] image = new int[bufferedImage.getHeight() * bufferedImage.getWidth()];
            int k = 0;
            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                for (int j = 0; j < bufferedImage.getWidth(); j++) {
                    image[k] = Integer.reverseBytes(Integer.rotateLeft(bufferedImage.getRGB(j, i), 8));
                    k++;
                }
            }
            return image;
        } catch (IOException e) {
            return new int[0];
        }
    }

    public static IntegerBuffer2D injectableTextureFromJar(String imagePathInJar, ClassLoader loader, int width) {
        return new IntegerBuffer2D(width, extractBytes(imagePathInJar, loader));
    }
}

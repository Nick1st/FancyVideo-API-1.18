package nick1st.fancyvideo.internal;

import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Util {
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
                    //image[k] = Integer.rotateLeft(color, 8);
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

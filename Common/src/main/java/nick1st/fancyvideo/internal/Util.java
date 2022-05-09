package nick1st.fancyvideo.internal;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.internal.AdvancedFrame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class Util {
    public static int[] extractBytes(String imageName, ClassLoader loader) {
        try (InputStream in = loader.getResourceAsStream(imageName)){
            if (in == null) {
                throw new IOException("Resource not found.");
            }
            BufferedImage bufferedImage = ImageIO.read(in);

            int[] image = new int[bufferedImage.getHeight() * bufferedImage.getWidth()];
            int k = 0;
            for (int i = 0; i < bufferedImage.getHeight(); i++) {
                for (int j = 0; j < bufferedImage.getWidth(); j++) {
                    image[k] = bufferedImage.getRGB(j, i);
                    k++;
                }
            }
            return image;
        } catch (IOException e) {
            return new int[0];
        }
    }

    public static AdvancedFrame injectableTextureFromJar(String imagePathInJar, ClassLoader loader, int width) {
        return new AdvancedFrame(extractBytes(imagePathInJar, loader), width);
    }

    public static AdvancedFrame drawFontToFrame(AdvancedFrame in) {
        PoseStack stack = new PoseStack();
        Minecraft.getInstance().font.draw(stack, "§f" + "Test", 0, 0, Mth.ceil(255.0F) << 24);
        Constants.LOG.info(stack.last().normal().toString());
        MatrixToIntArray(stack);
        return in;
    }

    public static int[] MatrixToIntArray(PoseStack poseStack) {
        Matrix4f matrix4f = poseStack.last().pose();
        BufferBuilder bb = Tesselator.getInstance().getBuilder();

        for (byte i : bb.popNextBuffer().getSecond().array()) {
            Constants.LOG.info(String.valueOf(i));
        }
        return new int[0];
    }
}

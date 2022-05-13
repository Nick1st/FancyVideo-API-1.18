package nick1st.fancyvideo.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import nick1st.fancyvideo.api.internal.AdvancedFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleTextRenderer {

    private static SimpleTextRenderer instance;

    private final AdvancedFrame FontFrame;
    private static final Map<Character, Integer> CHARACTER_X_OFFSET = new HashMap<>();
    private static final Map<Character, Integer> CHARACTER_HEIGHT_OFFSET = new HashMap<>();

    private SimpleTextRenderer(){
        FontFrame = Util.injectableTextureFromJar("assets/minecraft/textures/font/ascii.png", Minecraft.class.getClassLoader(), 128);

        CHARACTER_X_OFFSET.clear();

        CHARACTER_X_OFFSET.put(" ".charAt(0), -3);
        CHARACTER_X_OFFSET.put("!".charAt(0), -4);
        CHARACTER_X_OFFSET.put(".".charAt(0), -4);
        CHARACTER_X_OFFSET.put(",".charAt(0), -4);
        CHARACTER_X_OFFSET.put(":".charAt(0), -4);
        CHARACTER_X_OFFSET.put(";".charAt(0), -4);
        CHARACTER_X_OFFSET.put("*".charAt(0), -3);
        CHARACTER_X_OFFSET.put("'".charAt(0), -4);
        CHARACTER_X_OFFSET.put("´".charAt(0), -4);
        CHARACTER_X_OFFSET.put("`".charAt(0), -4);
        CHARACTER_X_OFFSET.put("}".charAt(0), -3);
        CHARACTER_X_OFFSET.put("{".charAt(0), -3);
        CHARACTER_X_OFFSET.put(")".charAt(0), -2);
        CHARACTER_X_OFFSET.put("(".charAt(0), -2);
        CHARACTER_X_OFFSET.put("]".charAt(0), -2);
        CHARACTER_X_OFFSET.put("[".charAt(0), -2);
        CHARACTER_X_OFFSET.put("i".charAt(0), -4);
        CHARACTER_X_OFFSET.put("I".charAt(0), -2);
        CHARACTER_X_OFFSET.put("l".charAt(0), -3);
        CHARACTER_X_OFFSET.put("t".charAt(0), -2);
        CHARACTER_X_OFFSET.put("k".charAt(0), -1);
        CHARACTER_X_OFFSET.put("|".charAt(0), -3);

        CHARACTER_HEIGHT_OFFSET.clear();

        CHARACTER_HEIGHT_OFFSET.put("p".charAt(0), 1);
        CHARACTER_HEIGHT_OFFSET.put("q".charAt(0), 1);
        CHARACTER_HEIGHT_OFFSET.put("y".charAt(0), 1);
        CHARACTER_HEIGHT_OFFSET.put("j".charAt(0), 1);
        CHARACTER_HEIGHT_OFFSET.put("g".charAt(0), 1);
        CHARACTER_HEIGHT_OFFSET.put("@".charAt(0), 1);
        CHARACTER_HEIGHT_OFFSET.put(",".charAt(0), 1);
    }

    public static synchronized SimpleTextRenderer getInstance() {
        if (instance == null) {
            instance = new SimpleTextRenderer();
        }
        return instance;
    }

    public AdvancedFrame drawString(String text, int rgbColor, float alpha, int scale) {

        float[] color = getColor(rgbColor);

        List<AdvancedFrame> chars = new ArrayList<>();

        int mostHeightOff = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c > 0xFF) {
                continue;
            }
            int charX = (c & 0x0F) * 8;
            int charY = (c >> 4 & 0x0F) * 8;
            int heightOffset = (CHARACTER_HEIGHT_OFFSET.containsKey(c)) ? heightOffset = CHARACTER_HEIGHT_OFFSET.get(c) : 0;
            mostHeightOff = Math.max(heightOffset, mostHeightOff);
            int cWidth = CHARACTER_X_OFFSET.getOrDefault(c, 8);


//            if (CHARACTER_X_OFFSET.containsKey(c)) {
//                xOffset += CHARACTER_X_OFFSET.get(c);
//            }

            int startingPoint = (charY * FontFrame.getWidth()) + charX;

            int[] charArray = new int[cWidth * (8 + heightOffset)];

            for (int xi = 0; xi < cWidth; xi++) {
                for (int yi = heightOffset; yi < 8; yi++) {
                    charArray[(yi * cWidth) + xi] = FontFrame.getFrame()[((charY + yi) * FontFrame.getWidth()) + (charX + xi)];
                }
            }

            chars.add(new AdvancedFrame(charArray, cWidth));


            //RenderUtils.bindTexture(DEFAULT_FONT);
            //matrix.pushPose();
            //RenderSystem.setShaderColor(color[0], color[1], color[2], alpha);
            //matrix.translate((x + ((i * 6) * scale)) + (xOffset * scale), y, 0);

            //matrix.scale(scale, scale, 0);
            //GuiComponent.blit(matrix, 0, 0, charX, charY, 6, 7 + heightOffset, 128, 128); //charX  charY  6  7  128  128


            //RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            //matrix.popPose();
        }

        int totalWidth = 0;
        for (AdvancedFrame frame : chars) {
            totalWidth += frame.getWidth();
        }

        //AdvancedFrame finalFrame = new AdvancedFrame(new int[totalWidth * (8 + mostHeightOff)], totalWidth);
        int[] finalArray = new int[totalWidth * (8 + mostHeightOff)];
        List<Integer> finalList = new ArrayList<>();

        for (int h = 0; h < (8 + mostHeightOff); h++) {
            for (int i = 0; i < chars.size(); i++) {
                AdvancedFrame tempF = chars.get(i);

                if ((tempF.getWidth() * h) < tempF.getFrame().length) {
                    for (int j = 0; j < tempF.getWidth(); j++) {
                        finalList.add(tempF.getFrame()[(h * tempF.getWidth()) + j]);
                    }
                }
                //int tempW = tempF.getWidth();
                //int tempH = tempF.getFrame().length / tempW;
                //for (int j=0; j < tempW; j++) {
                //}
            }
        }

        return new AdvancedFrame(convert(finalList.toArray()), totalWidth);
    }

    public static int[] convert(Object[] objectArray){
        int[] intArray = new int[objectArray.length];

        for(int i=0; i<objectArray.length; i++){
            intArray[i] = (int) objectArray[i];
        }

        return intArray;
    }

    public static void drawStringWithShadow(PoseStack matrix, String text, int x, int y, int rgbColor, float alpha, float scale) {
        //draw shadow
        //drawString(matrix, text, x + Math.max((int)(1 * scale), 1), y + Math.max((int)(1 * scale), 1), 0, alpha / 2.0F, scale);
        //draw normal text
        //drawString(matrix, text, x, y, rgbColor, alpha, scale);
    }

    public static int getStringWidth(String text) {
        int length = 0;
        for (char c : text.toCharArray()) {
            int i = 6;
            if (CHARACTER_X_OFFSET.containsKey(c)) {
                i += CHARACTER_X_OFFSET.get(c);
            }
            length += i;
        }
        return length;
    }

    public static int getStringHeight() {
        return 7;
    }

    protected static float[] getColor(int rgb) {
        float[] color = new float[] { 0.0f, 0.0f, 0.0f};
        color[2] = ((rgb) & 0xFF) / 255.0f;
        color[1] = ((rgb >> 8 ) & 0xFF) / 255.0f;
        color[0] = ((rgb >> 16 ) & 0xFF) / 255.0f;
        return color;
    }

}

package nick1st.fancyvideo.internal;

import net.minecraft.client.Minecraft;
import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>This is not yet production ready. Use with caution.</b>
 */
public class SimpleTextRenderer {

    private static SimpleTextRenderer instance;

    private final IntegerBuffer2D FontFrame;
    private static final Map<Character, Integer> CHARACTER_X_OFFSET = new HashMap<>();
    private static final Map<Character, Integer> CHARACTER_HEIGHT_OFFSET = new HashMap<>();

    private SimpleTextRenderer(){
        FontFrame = Util.injectableTextureFromJar("assets/minecraft/textures/font/ascii.png", Minecraft.class.getClassLoader(), 128);

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

    public IntegerBuffer2D drawString(String text, int rgbColor, int scale) {
        IntegerBuffer2D[] chars = new IntegerBuffer2D[text.length()];

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c > 0xFF) {
                continue;
            }
            int charX = (c & 0x0F) * 8;
            int charY = (c >> 4 & 0x0F) * 8;
            int heightOffset = CHARACTER_HEIGHT_OFFSET.getOrDefault(c, 0);
            int cWidth = CHARACTER_X_OFFSET.getOrDefault(c, 0);

            chars[i] = FontFrame.bulkGet(charX, charY, 8 + cWidth, 8 + heightOffset);
        }

        IntegerBuffer2D temp = IntegerBuffer2D.join(chars, (short)0);
        temp.replaceValues(-1, rgbColor, 16777215, 0);
        temp.scale(scale);
        return temp;
    }

    //public static void drawStringWithShadow(PoseStack matrix, String text, int x, int y, int rgbColor, float alpha, float scale) { //TODO reimplement this
        //draw shadow
        //drawString(matrix, text, x + Math.max((int)(1 * scale), 1), y + Math.max((int)(1 * scale), 1), 0, alpha / 2.0F, scale);
        //draw normal text
        //drawString(matrix, text, x, y, rgbColor, alpha, scale);
    //}

    public static int getStringWidth(String text) {
        int length = 0;
        for (char c : text.toCharArray()) {
            length += CHARACTER_X_OFFSET.containsKey(c) ? 8 - CHARACTER_X_OFFSET.get(c) : 8;
        }
        return length;
    }

    public static int getStringHeight(String text) {
        int mostHeightOff = 0;

        for (char c : text.toCharArray()) {
            int heightOffset = CHARACTER_HEIGHT_OFFSET.getOrDefault(c, 0);
            mostHeightOff = Math.max(heightOffset, mostHeightOff);
        }
        return 8 + mostHeightOff;
    }


    /**
     * <b>DO NOT USE THIS, IT'S BUGGED AS FUCK</b>
     * @param text
     * @param sizeX
     * @param sizeY
     * @param color
     * @return
     */
    public static IntegerBuffer2D greatestSizedText(String text, int sizeX, int sizeY, int color) { //TODO Fix the logic @Nick1st
        int oneLineHeight = getStringHeight(text);
        int stringWidth = getStringWidth(text);
        float bestRatioForCalcY = (float) sizeY / sizeX;

        String[] words = text.split(" ");
        int maxLength = 0;
        for (String word: words) {
            maxLength = Math.max(getStringWidth(word), maxLength);
        }

        //int optimalWidthPerLine = Math.max((int) Math.floor(Math.sqrt(((float) sizeX / sizeY) * stringWidth)), maxLength);
        //int optimalWidthPerLine = Math.max((int) Math.floor(((float) sizeX / sizeY) * stringWidth), maxLength);
        int optimalWidthPerLine = Math.max((int) Math.floor(Math.sqrt((float) sizeX / sizeY) * (stringWidth / (float) sizeX / sizeY)), maxLength);
        int optimalNumberOfLines = (int) Math.floor(Math.sqrt(((float) sizeY / sizeX) * stringWidth)); //Replaced Ceil with floor

        List<String> lines = new ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        for (String word: words) {
            if (getStringWidth(currentLine.toString()) + getStringWidth(word) < optimalWidthPerLine) {
                if (currentLine.length() != 0) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        lines.add(currentLine.toString());

        IntegerBuffer2D toReturn = new IntegerBuffer2D(sizeX, sizeY);
        int startY = 0;
        for (int i = 0; i < lines.size(); i++) {
            IntegerBuffer2D buffer = getInstance().drawString(lines.get(i), color, Math.min(sizeX / optimalWidthPerLine, (sizeY / lines.size()) / oneLineHeight));
            toReturn.bulkPut(buffer, (sizeX - buffer.getWidth()) / 2, startY, true);
            startY += buffer.getHeight();
        }

        return toReturn;
    }
}

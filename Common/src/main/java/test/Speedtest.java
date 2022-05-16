package test;

import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Speedtest {
    static Random r = new Random();

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter("DebugOutput.txt", StandardCharsets.UTF_8)) {
            for (int i = 0; i < 10; i++) {
                int sizeX = r.nextInt(2,7680);
                int sizeY = r.nextInt(2,7680);
                int sizeX2 = r.nextInt(sizeX);
                int sizeY2 = r.nextInt(sizeY);
                int offX = r.nextInt(sizeX - sizeX2);
                int offY = r.nextInt(sizeY - sizeY2);
                writer.println(test(sizeX, sizeY, sizeX2, sizeY2, offX, offY) + "," + sizeX + "," + sizeY + ","
                        + sizeX2 + "," + sizeY2);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println("Total is: " + total);
    }

    public static long test(int sizeX, int sizeY, int sizeX2, int sixeY2, int offX, int offY) {
        //System.out.println("Generating Test set...");
        //long startTime = System.nanoTime();
        IntegerBuffer2D b1_1 = new IntegerBuffer2D(1080, 1920);
        IntegerBuffer2D b1_2 = new IntegerBuffer2D(1080, 1920);
        for (int i = 0; i < 1920; i++) {
            for (int j = 0; j < 1080; j++) {
                int value = r.nextInt(Integer.MAX_VALUE);
                b1_1.put(value, j, i);
                b1_2.put(value, j, i);
            }
        }
        //long stopTime = System.nanoTime();
        //System.out.println("Test set generated after Nanos: " + (stopTime - startTime));

        //System.out.println("Generating 2.Test set...");
        //startTime = System.nanoTime();
        IntegerBuffer2D b2_1 = new IntegerBuffer2D(720, 1080);
        for (int i = 0; i < 1080; i++) {
            for (int j = 0; j < 720; j++) {
                b2_1.put(r.nextInt(Integer.MAX_VALUE), j, i);
            }
        }
        //stopTime = System.nanoTime();
        //System.out.println("2. Test set generated after Nanos: " + (stopTime - startTime));

        //System.out.println("Running Tests...");
        long startTime1 = System.nanoTime();
        b1_1.bulkPut(b2_1, 11, 11, true);
        long stopTime1 = System.nanoTime();
        long startTime2 = System.nanoTime();
        //b1_2.bulkPut2(b2_1, 11, 11, true);
        long stopTime2 = System.nanoTime();
        //System.out.println("Tests finished!");
        //System.out.println("First Test (bulk): " + (stopTime1 - startTime1));
        //System.out.println("Second Test (bulk2): " + (stopTime2 - startTime2));

        System.out.println("Difference: " + ((stopTime2 - startTime2) - (stopTime1 - startTime1)));
        return (stopTime2 - startTime2) - (stopTime1 - startTime1);
    }
}

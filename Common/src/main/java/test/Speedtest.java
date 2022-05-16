package test;

import nick1st.fancyvideo.api.internal.utils.IntegerBuffer2D;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Speedtest {
    static Random r = new Random();

    public static void main(String[] args) {
        try (PrintWriter writer = new PrintWriter("DebugOutput.txt", StandardCharsets.UTF_8)) {
            writer.println("TimeDelta,sizeX,sizeY,sizeX2,sizeY2");
            for (int i = 0; i < 1000; i++) {
                int sizeX = r.nextInt(2,7680);
                int sizeY = r.nextInt(2,7680);
                int sizeX2 = r.nextInt(sizeX);
                int sizeY2 = r.nextInt(sizeY);
                if (sizeX2 == 0)
                    sizeX2++;
                if (sizeY2 == 0)
                    sizeY2++;
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

    public static long test(int sizeX, int sizeY, int sizeX2, int sizeY2, int offX, int offY) {
        //System.out.println("Generating Test set...");
        //long startTime = System.nanoTime();
        IntegerBuffer2D b1_1 = new IntegerBuffer2D(sizeX, sizeY);
        IntegerBuffer2D b1_2 = new IntegerBuffer2D(sizeX, sizeY);
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                int value = r.nextInt(Integer.MAX_VALUE);
                b1_1.put(value, i, j);
                b1_2.put(value, i, j);
            }
        }
        //long stopTime = System.nanoTime();
        //System.out.println("> Test set generated after Nanos: " + (stopTime - startTime));

        //System.out.println("Generating 2.Test set...");
        //startTime = System.nanoTime();
        IntegerBuffer2D b2_1 = new IntegerBuffer2D(sizeX2, sizeY2);
        for (int i = 0; i < sizeX2; i++) {
            for (int j = 0; j < sizeY2; j++) {
                b2_1.put(r.nextInt(Integer.MAX_VALUE), i, j);
            }
        }
        //stopTime = System.nanoTime();
        //System.out.println("2. Test set generated after Nanos: " + (stopTime - startTime));

        //System.out.println("Running Tests...");
        System.gc();
        long startTime1 = System.nanoTime();
        b1_1.bulkPut(b2_1, offX, offY, true);
        long stopTime1 = System.nanoTime();
        System.gc();
        long startTime2 = System.nanoTime();
        b1_2.bulkPut(b2_1, offX, offY, false);
        long stopTime2 = System.nanoTime();
        //System.out.println("Tests finished!");
        //System.out.println("First Test (bulk): " + (stopTime1 - startTime1));
        //System.out.println("Second Test (bulk2): " + (stopTime2 - startTime2));

        //System.out.println("Difference: " + ((stopTime2 - startTime2) - (stopTime1 - startTime1)));
        return (stopTime2 - startTime2) - (stopTime1 - startTime1);
    }
}

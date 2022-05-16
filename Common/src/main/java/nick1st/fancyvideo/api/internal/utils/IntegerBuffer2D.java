package nick1st.fancyvideo.api.internal.utils;

import nick1st.fancyvideo.api.internal.AdvancedFrame;

import java.util.Arrays;
import java.util.stream.IntStream;

public class IntegerBuffer2D {
    int[][] matrix;
    //IntBuffer[] banks;

    public IntegerBuffer2D(int banks, int sizeOfBanks) {
        matrix = new int[banks][sizeOfBanks];
        //this.banks = new IntBuffer[banks];
        //Arrays.fill(this.banks, IntBuffer.allocate(sizeOfBanks));
    }

    public void grow(int sizeToGrowX, int sizeToGrowY) {
        if (sizeToGrowY > 0) {
            //int oldLength = banks.length;
            //banks = Arrays.copyOf(banks, oldLength + sizeToGrowY);
            //Arrays.fill(banks, oldLength, oldLength + sizeToGrowY, IntBuffer.allocate(banks[0].limit()));
            int oldLength = matrix.length;
            matrix = Arrays.copyOf(matrix, oldLength +sizeToGrowY);
            Arrays.fill(matrix, oldLength, oldLength + sizeToGrowY, new int[matrix[0].length]);
        }
        if (sizeToGrowX > 0) {
//            for (int i = 0; i < banks.length; i++) {
//                banks[i] = IntBuffer.wrap(banks[i].array());
//            }
            for (int i = 0; i < matrix.length; i++) {
                matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length + sizeToGrowY);
            }
        }
    }

    public void bulkPut(AdvancedFrame[] frameArray) {

    }

    public void bulkPut(IntegerBuffer2D bufferToOverlay, int startX, int startY) {
        bulkPut(bufferToOverlay, startX, startY, true);
    }

//    public void bulkPut(IntegerBuffer2D bufferToOverlay, int startX, int startY, boolean skipZeros) {
//        int k = 0;
//        for (int i = startY; i < bufferToOverlay.banks.length; i++) {
//            for (int j = startX; j < bufferToOverlay.banks[0].array().length + startX; j++) {
//                int value = bufferToOverlay.banks[k].get(j - startX);
//                if (value != 0) {
//                    banks[i].put(j, value);
//                }
//            }
//            k++;
//        }
//
//        for (int i = startY; i < bufferToOverlay.matrix.length + startY; i++) {
//            for (int j = startX; j < bufferToOverlay.matrix[0].length + startX; j++) {
//                int value = bufferToOverlay.matrix[k][j - startX];
//                if (value != 0) {
//                    matrix[i][j] = value;
//                }
//            }
//        }
//
//        IntStream.range(0, bufferToOverlay.matrix.length).forEach( i ->
//                IntStream.range(0, bufferToOverlay.matrix[0].length).forEach(j -> {
//                    int value = bufferToOverlay.matrix[i][j];
//                    if (!skipZeros || value != 0) {
//                        matrix[i + startY][j + startX] = bufferToOverlay.matrix[i][j];
//                    }
//                }));
//    }

    public void bulkPut(IntegerBuffer2D bufferToOverlay, int startX, int startY, boolean skipZeros) {
        int k = 0;
        for (int i = startY; i < bufferToOverlay.matrix.length + startY; i++) {
            for (int j = startX; j < bufferToOverlay.matrix[0].length + startX; j++) {
                int value = bufferToOverlay.matrix[k][j - startX];
                if (!skipZeros || value != 0) {
                    matrix[i][j] = value;
                }
            }
            k++;
        }
    }

    public void put(int value, int posY, int posX) {
        matrix[posY][posX] = value;
    }

    public void fill(int value) {
        fill(value, true);
    }

    public void fill(int value, boolean onlyZeros) {
//        for (int i = 0; i < banks.length; i++) {
//            for (int j = 0; j < banks[0].array().length; j++) {
//                if (banks[i].get(j) == 0) {
//                    banks[i].put(j, value);
//                }
//            }
//        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = value;
                }
            }
        }
    }

    @Override
    public String toString() {
//        StringBuilder s = new StringBuilder("IntegerBuffer2D{\n");
//        for (int i = 0; i < banks.length; i++) {
//            int cap = banks[0].capacity();
//            s.append("[");
//            for (int j = 0; j < cap; j++) {
//                s.append(banks[i].get(j));
//                     s.append((j != cap - 1) ? "," : "]\n");
//            }
//        }
//        s.append("}");
//        return s.toString();

        StringBuilder s = new StringBuilder("IntegerBuffer2D{\n");
        for (int i = 0; i < matrix.length; i++) {
            int cap = matrix[0].length;
            s.append("[");
            for (int j = 0; j < cap; j++) {
                s.append(matrix[i][j]);
                s.append((j != cap - 1) ? "," : "]\n");
            }
        }
        s.append("}");
        return s.toString();
    }
}
package nick1st.fancyvideo.api.internal.utils;

import nick1st.fancyvideo.api.internal.AdvancedFrame;

import java.util.Arrays;

public class IntegerBuffer2D {
    int[][] matrix;

    public IntegerBuffer2D(int sizeOfBanks, int banks) {
        matrix = new int[banks][sizeOfBanks];
    }

    public IntegerBuffer2D(IntegerBuffer2D toCopy) {
        this(toCopy.getWidth(), toCopy.getHeight());
        System.arraycopy(toCopy.matrix, 0, matrix, 0, toCopy.getHeight());
    }

    public IntegerBuffer2D(int width, int[] frame) {
        this(width, frame.length / width);
        for (int i = 0; i < frame.length / width; i++) {
            System.arraycopy(frame, i * width, matrix[i], 0, width);
        }
    }

    public IntegerBuffer2D(AdvancedFrame frame) {
        this(frame.getWidth(), frame.getFrame().length / frame.getWidth());
        for (int i = 0; i < frame.getFrame().length / frame.getWidth(); i++) {
            System.arraycopy(frame.getFrame(), i * frame.getWidth(), matrix[i], 0, frame.getWidth());
        }
    }

    public void grow(int sizeToGrowX, int sizeToGrowY) {
        if (sizeToGrowY > 0) {
            matrix = Arrays.copyOf(matrix, matrix.length +sizeToGrowY);
        }
        if (sizeToGrowX > 0) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length + sizeToGrowY);
            }
        }
    }

    /**
     * This will replace all contents of this buffer
     */
    public void bulkSet(int[] intArray) {
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(intArray, i * matrix[0].length, matrix[i], 0, matrix[0].length);
        }
    }

    public void bulkPut(IntegerBuffer2D bufferToOverlay, int startX, int startY, boolean skipZeros) {
        int k = 0;
        for (int i = startY; i < bufferToOverlay.matrix.length + startY; i++) {
            if (skipZeros) {
                for (int j = startX; j < bufferToOverlay.matrix[0].length + startX; j++) {
                    int value = bufferToOverlay.matrix[k][j - startX];
                    if (value != 0) {
                        matrix[i][j] = value;
                    }
                }
            } else {
                System.arraycopy(bufferToOverlay.matrix[k], 0, matrix[i], startX, bufferToOverlay.matrix[k].length);
            }
            k++;

        }
    }

    public int[] getArray() {
        int[] flattened = new int[matrix.length * matrix[0].length];
        for (int i = 0; i < getHeight(); i++) {
            System.arraycopy(matrix[i], 0 , flattened, i * getWidth(), getWidth());
        }
        return flattened;
    }

    public void put(int value, int posX, int posY) {
        matrix[posY][posX] = value;
    }

    public int get(int x, int y) {
        return matrix[y][x];
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
                if (!onlyZeros || matrix[i][j] == 0) {
                    matrix[i][j] = value;
                }
            }
        }
    }

    public int getWidth() {
        return matrix[0].length;
    }

    public int getHeight() {
        return matrix.length;
    }
}
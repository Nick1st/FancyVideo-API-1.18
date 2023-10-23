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

import java.nio.IntBuffer;
import java.util.Arrays;

@SuppressWarnings("unused")
public class IntegerBuffer2D {
    int[][] matrix; // Height, Width

    public IntegerBuffer2D(int sizeOfBanks, int banks) { // Width, Height
        matrix = new int[banks][sizeOfBanks];
    }

    public IntegerBuffer2D(int width, IntBuffer intBuffer) {
        this(width, intBuffer.capacity() / width);
        int height = intBuffer.capacity() / width;
        for (int i = 0; i < height; i++) {
            int[] bank = new int[width];
            intBuffer.get(i * width, bank, 0, width);
            matrix[i] = bank;
        }
    }

    @SuppressWarnings("This does copy matrix, but using low-level code")
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

    public void replaceValues(int val1, int rep1, int val2, int rep2) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == val1) {
                    matrix[i][j] = rep1;
                } else if (matrix[i][j] == val2) {
                    matrix[i][j] = rep2;
                }
            }

        }
    }

    public void scale(int size) {
        IntegerBuffer2D temp = new IntegerBuffer2D(this);
        grow(matrix[0].length * (size - 1), matrix.length * (size - 1));
        for (int i = 0; i < (temp.matrix.length); i++) {
            for (int j = 0; j < (temp.matrix[0].length); j++) {
                for (int k = 0; k < size; k++) {
                    for (int l = 0; l < size; l++) {
                        matrix[(i*size)+k][(j*size)+l] = temp.matrix[i][j];
                    }
                }
            }
        }
    }

    public void grow(int sizeToGrowX, int sizeToGrowY) {
        if (sizeToGrowY > 0) {
            matrix = Arrays.copyOf(matrix, matrix.length + sizeToGrowY);
            Arrays.fill(matrix, matrix.length - sizeToGrowY, matrix.length, new int[matrix[0].length]);
        }
        if (sizeToGrowX > 0) {
            for (int i = 0; i < matrix.length; i++) {
                matrix[i] = Arrays.copyOf(matrix[i], matrix[i].length + sizeToGrowX);
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

    public IntegerBuffer2D bulkGet(int x, int y, int sizeX, int sizeY) {
        IntegerBuffer2D temp = new IntegerBuffer2D(sizeX, sizeY);
        for (int i = 0; i < sizeY; i++) {
            System.arraycopy(matrix[i + y], x, temp.matrix[i], 0, sizeX);
        }
        return temp;
    }

    /**
     *
     * @param toJoin All Arrays that should be joined together.
     * @param side Only accepts 0 or 1. 0 for left, 1 for bottom.
     * @return A joined buffer
     */
    public static IntegerBuffer2D join(IntegerBuffer2D[] toJoin, short side) {
        if (side != 0 && side != 1) {
            throw new IllegalArgumentException("Side only allows 0 or 1");
        }
        int totalWidth = 0;
        int totalHeight = 0;

        for (IntegerBuffer2D buffer: toJoin) {
            if (side == 0) {
                totalHeight = Math.max(buffer.getHeight(), totalHeight);
                totalWidth += buffer.getWidth();
            } else {
                totalWidth = Math.max(buffer.getWidth(), totalWidth);
                totalHeight += buffer.getHeight();
            }
        }

        IntegerBuffer2D toReturn = new IntegerBuffer2D(totalWidth, totalHeight);

        int i = 0;

        for (IntegerBuffer2D buffer: toJoin) {
            if (side == 0) {
                toReturn.bulkPut(buffer, i,0, false);
                i += buffer.getWidth();
            } else {
                toReturn.bulkPut(buffer, 0,i, false);
                i += buffer.getHeight();
            }
        }

        return toReturn;
    }

    public void fill(int value, boolean onlyZeros) {
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
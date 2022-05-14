package nick1st.fancyvideo.api.internal.utils;

import nick1st.fancyvideo.api.internal.AdvancedFrame;

import java.nio.IntBuffer;
import java.util.Arrays;

public class IntegerBuffer2D {
    IntBuffer[] banks;

    public IntegerBuffer2D(int banks, int sizeOfBanks) {
        this.banks = new IntBuffer[banks];
        Arrays.fill(this.banks, IntBuffer.allocate(sizeOfBanks));
    }

    public void grow(int sizeToGrowX, int sizeToGrowY) {
        if (sizeToGrowY > 0) {
            int oldLength = banks.length;
            banks = Arrays.copyOf(banks, oldLength + sizeToGrowY);
            Arrays.fill(banks, oldLength, oldLength + sizeToGrowY, IntBuffer.allocate(banks[0].limit()));
        }
        if (sizeToGrowX > 0) {
            for (int i = 0; i < banks.length; i++) {
                banks[i] = IntBuffer.wrap(banks[i].array());
            }
        }
    }

    public void bulkPut(AdvancedFrame[] frameArray) {

    }

    public void bulkPut(IntegerBuffer2D bufferToOverlay, int startX, int startY) {
        bulkPut(bufferToOverlay, startX, startY, true);
    }

    public void bulkPut(IntegerBuffer2D bufferToOverlay, int startX, int startY, boolean skipZeros) {
        int k = 0;
        for (int i = startY; i < bufferToOverlay.banks.length; i++) {
            for (int j = startX; j < bufferToOverlay.banks[0].array().length; j++) {
                int value = bufferToOverlay.banks[k].get(j);
                if (value != 0) {
                    banks[i].put(j, value);
                }
            }
            k++;
        }
    }

    public void fill(int value) {
        fill(value, true);
    }

    public void fill(int value, boolean onlyZeros) {
        for (int i = 0; i < banks.length; i++) {
            for (int j = 0; j < banks[0].array().length; j++) {
                if (banks[i].get(j) == 0) {
                    banks[i].put(j, value);
                }
            }
        }
    }

    @Override
    public String toString() {
        String s = "IntegerBuffer2D{\n";
        for (int i = 0; i < banks.length; i++) {
            int cap = banks[0].capacity();
            s += "[";
            for (int j = 0; j < cap; j++) {
                s += banks[i].get(j);
                     s += (j != cap - 1) ? "," : "]\n";
            }
        }
        s += "}";
        return s;
    }
}
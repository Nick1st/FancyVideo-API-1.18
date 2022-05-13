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

    public void growBanks(int sizeToGrow) {
        int oldLength = banks.length;
        banks = Arrays.copyOf(banks, oldLength + sizeToGrow);
        Arrays.fill(banks, oldLength, oldLength + sizeToGrow, IntBuffer.allocate(banks[0].limit()));
    }

    public void bulkPut(AdvancedFrame[] frameArray) {

    }
}

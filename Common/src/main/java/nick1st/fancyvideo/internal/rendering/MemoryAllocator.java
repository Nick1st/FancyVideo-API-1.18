package nick1st.fancyvideo.internal.rendering;

import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

/**
 * Helper class using LWJGL's MemoryAllocation.
 * @apiNote Unstable API: Uses unstable API calls
 * @since 3.0.0
 */
public class MemoryAllocator {
    private static final MemoryUtil.MemoryAllocator ALLOCATOR = MemoryUtil.getAllocator(false);

    /**
     * Allocates a new direct ByteBuffer.
     * @param size The size (in bytes) of the newly allocated buffer.
     * @return A direct ByteBuffer.
     * @throws OutOfMemoryError If no memory could be allocated due to no more memory being available.
     * @since 3.0.0
     */
    public static ByteBuffer alloc(int size) {
        long p = ALLOCATOR.malloc(size);
        if (p == 0L)
            throw new OutOfMemoryError("FancyVideo-API failed to allocate " + size + " bytes.");
        return MemoryUtil.memByteBuffer(p, size);
    }

    /**
     * Relocates a direct ByteBuffer, allowing it to change its allocated size.
     * @param byteBuffer the ByteBuffer to resize
     * @param newSize the new size the ByteBuffer should get
     * @return a new direct ByteBuffer
     * @throws OutOfMemoryError If no memory could be allocated due to no more memory being available.
     * @since 3.0.0
     */
    public static ByteBuffer resize(ByteBuffer byteBuffer, int newSize) {
        long p = ALLOCATOR.realloc(MemoryUtil.memAddress0(byteBuffer), newSize);
        if (p == 0L)
            throw new OutOfMemoryError("FancyVideo-API failed to reallocate a buffer from " + byteBuffer.capacity() +
                    " bytes to a new capacity of " + newSize + " bytes.");
        return MemoryUtil.memByteBuffer(p, newSize);
    }
}

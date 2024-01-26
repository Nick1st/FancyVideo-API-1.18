package nick1st.fancyvideo.api.helpers.formats;

import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

/**
 * A format that is based around a byte buffer. The application receiving the buffer must take care of how to use it.
 * Base VideoFormat. All methods in this class that operate on ByteBuffers rewind them before usage/return.
 * @apiNote Custom player implementations should try to always support this very basic format
 * @since 3.0.0
 */
public final class ByteBufferVideoFormat extends VideoFormatNew<ByteBufferVideoFormat> implements FormatConversion.BaseMediaFormat<ByteBufferVideoFormat> {

    public final Type Type = ByteBufferVideoFormat.class;

    /**
     * Height of the frame
     * @since 3.0.0
     */
    private int height;

    /**
     * Setting this means the frame source can freely set the height field
     * @since 3.0.0
     */
    public final boolean autoHeight;

    /**
     * Width of the frame
     * @since 3.0.0
     */
    private int width;

    /**
     * Setting this means the frame source can freely set the width field
     * @since 3.0.0
     */
    public final boolean autoWidth;

    /**
     * Order of the bytes in the buffer
     * @since 3.0.0
     */
    public final ByteOrder byteOrder;

    /**
     * The buffer itself
     * @since 3.0.0
     */
    private ByteBuffer buffer;

    /**
     * Callback that writes the buffer
     * @since 3.0.0
     */
    private Consumer<ByteBuffer> bufferWriteCallback;

    /**
     * Callback called after a new frame was set
     * @since 3.0.0
     */
    private Runnable newFrameWasSet;

    /**
     * Public ctor constructing a new ByteBufferVideoFormat
     * @param width Sets the initial buffer width. -1 to allow the frame setter to set this value. Must be >= -1.
     * @param height Sets the initial buffer height. -1 to allow the frame setter to set this value. Must be >= -1.
     * @param byteOrder ByteOrder this buffer should use
     */
    public ByteBufferVideoFormat(int width, int height, ByteOrder byteOrder) {
        if (width < -1 || height < -1) {
            throw new IllegalArgumentException("width or height out of range. Must be >= -1");
        }
        this.autoWidth = width == -1;
        this.autoHeight = height == -1;
        this.width = width == -1 ? 1 : width;
        this.height = height == -1 ? 1 : height;
        this.byteOrder = byteOrder;
        this.buffer = ByteBuffer.allocate(width * height * byteOrder.byteCount);
    }

    /**
     * Sets a new buffer by invoking the bufferWriteCallback, then runs the newFrameSet Runnable to potentially call a
     * method reading from this buffer.
     * @since 3.0.0
     */
    public void setBuffer() {
        synchronized (this) {
            buffer.rewind();
            bufferWriteCallback.accept(buffer);
        }
        newFrameWasSet.run();
    }

    /**
     * Passes the buffer to you and allocates a new one for the buffer writer.
     * @apiNote If you call this twice before a new frame was set you'll get an empty buffer.
     * @return the old underlying, modifiable buffer
     * @since 3.0.0
     */
    public synchronized ByteBuffer drainBuffer() {
        ByteBuffer toReturn = buffer;
        buffer = ByteBuffer.allocate(buffer.capacity());
        return toReturn.rewind();
    }

    /**
     * Returns a readonly buffer accessing the underlying memory. The buffer content is changed by the buffer write
     * callback.
     * @apiNote The buffer you receive can be externally modified, even during read operations on it!
     * @return a readonly access to the underlying buffer
     * @since 3.0.0
     */
    public ByteBuffer unsafeReadAccess() {
        return buffer.asReadOnlyBuffer().rewind();
    }

    /**
     * Provides a readonly buffer accessing the underlying memory. Every other operation on the underlying memory is #
     * blocked until this returns.
     * @apiNote Make sure to not block the thread for too long
     * @param functionToExecute The function to execute
     * @since 3.0.0
     */
    public synchronized void freezeAndExecute(Consumer<ByteBuffer> functionToExecute) {
        functionToExecute.accept(buffer.asReadOnlyBuffer().rewind());
    }

    /**
     * Resizes the buffer
     * @apiNote Drops the current buffer and allocates a new one.
     * @param width the new width
     * @param height the new height
     * @since 3.0.0
     */
    public synchronized void resizeBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        buffer = ByteBuffer.allocate(width * height * byteOrder.byteCount);
    }

    /**
     * Sets a consumer for a method that writes to the buffer
     * @param bufferWriteCallback the consumer that writes to the buffer
     * @since 3.0.0
     */
    public void setBufferWriteCallback(Consumer<ByteBuffer> bufferWriteCallback) {
        this.bufferWriteCallback = bufferWriteCallback;
    }

    /**
     * Sets a runnable that is called once a new frame was set
     * @param newFrameWasSet The runnable to call
     * @since 3.0.0
     */
    public void setNewFrameWasSet(Runnable newFrameWasSet) {
        this.newFrameWasSet = newFrameWasSet;
    }
}

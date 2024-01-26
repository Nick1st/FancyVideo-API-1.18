package nick1st.fancyvideo.api.helpers.formats;

/**
 * Commonly used ByteOrders
 * @since 3.0.0
 */
public enum ByteOrder {
    ARGB(4),
    RGB(3),
    RGBA(4),
    ABGR(4),
    BGR(3),
    BGRA(4);

    public final int byteCount;

    ByteOrder(int byteCount) {
        this.byteCount = byteCount;
    }
}

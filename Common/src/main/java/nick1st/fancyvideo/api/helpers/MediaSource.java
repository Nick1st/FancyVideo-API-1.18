package nick1st.fancyvideo.api.helpers;

import java.net.URI;

/**
 * Contains all the required information to load the specified media source.
 * @since 3.0.0
 */
public class MediaSource {

    /**
     * The URI where the media is located;
     * @since 3.0.0
     */
    public final URI mediaURI;

    /**
     * @param mediaURI the URI where the media is located
     * @since 3.0.0
     */
    public MediaSource(URI mediaURI) {
        this.mediaURI = mediaURI;
    }
}

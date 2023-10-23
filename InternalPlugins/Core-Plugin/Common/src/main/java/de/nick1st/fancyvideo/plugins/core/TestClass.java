package de.nick1st.fancyvideo.plugins.core;

import nick1st.fancyvideo.Constants;
import org.freedesktop.gstreamer.Gst;
import org.freedesktop.gstreamer.Version;

public class TestClass {
    {
        new Constants();
    }

    public TestClass() {
        System.err.println("WTF");
        String[] args = new String[]{ "--gst-enable-gst-debug", "--gst-debug=gldisplay:3" };
        Gst.init(Version.of(1, 10), "BasicPipeline", args);
    }
}

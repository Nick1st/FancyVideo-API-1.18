package nick1st.fancyvideo.internal.natives;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class WindowsDiscovery {
    Map<Path, Boolean> vlcPaths = new HashMap<>();

    public interface Strategy {

        Map<Path, Boolean> discover();
    }
}

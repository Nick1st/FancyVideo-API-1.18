package nick1st.fancyvideo.internal;

import nick1st.fancyvideo.Constants;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;

import static nick1st.fancyvideo.Constants.PLUGINSDIR;

public class DLLHandler {
    public static void clearDLL() {
        try {
            new File(LibraryMapping.libVLC.getByOS(Constants.OS)).delete();
            new File(LibraryMapping.libVLCCore.getByOS(Constants.OS)).delete();
            FileUtils.deleteDirectory(new File("plugins"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean unpack(ClassLoader loader) {
        // Check if we package this os and arch
        String path = "vlc-bin/" + Constants.OS + "/" + Constants.ARCH + "/";
        Constants.LOG.debug(path);
        if (Constants.OS == null || loader.getResource(path) == null) {
            return false;
        }

        // Extract natives
        for (LibraryMapping mapping : LibraryMapping.values()) {
            String file;
            switch (Constants.OS) {
                case ("linux"):
                    file = mapping.linuxName;
                    break;
                case ("mac"):
                    file = mapping.macName;
                    break;
                case ("windows"):
                    file = mapping.windowsName;
                    break;
                default:
                    return false;
            }
            try {
                //noinspection ResultOfMethodCallIgnored
                new File(PLUGINSDIR).mkdir();
                extract(loader, path, file, mapping.isPlugin);
            } catch (IOException e) {
                Constants.LOG.error("An error occurred whilst trying to unpack natives ", e);
            }
        }
        return true;
    }

    private static void extract(ClassLoader loader, String path, String file, boolean isPlugin) throws IOException {
        if (isPlugin) {
            //noinspection ResultOfMethodCallIgnored
            new File(PLUGINSDIR + file).getParentFile().mkdirs();
        }
        InputStream in = isPlugin ? loader.getResourceAsStream(path + PLUGINSDIR + file) : loader.getResourceAsStream(path + file);
        OutputStream out = isPlugin ? new FileOutputStream(PLUGINSDIR + file) : new FileOutputStream(file);
        IOUtils.copy(in, out);
        in.close();
        out.flush();
        out.close();
    }
}

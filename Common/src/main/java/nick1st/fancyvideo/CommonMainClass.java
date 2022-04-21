package nick1st.fancyvideo;

import com.sun.jna.NativeLibrary;
import nick1st.fancyvideo.config.SimpleConfig;
import nick1st.fancyvideo.internal.Arch;
import nick1st.fancyvideo.internal.DLLHandler;
import nick1st.fancyvideo.internal.LibraryMapping;
import org.apache.commons.lang3.SystemUtils;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.factory.NativeLibraryMappingException;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;
import uk.co.caprica.vlcj.support.version.LibVlcVersion;
import uk.co.caprica.vlcj.support.version.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static uk.co.caprica.vlcj.binding.LibVlc.libvlc_get_version;

public class CommonMainClass {
    private final NativeDiscovery discovery = new NativeDiscovery();

    public CommonMainClass(SimpleConfig config) {
        // Detect OS
        if (SystemUtils.IS_OS_LINUX) {
            Constants.OS = "linux";
        } else if (SystemUtils.IS_OS_MAC) {
            Constants.OS = "mac";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            Constants.OS = "windows";
        } else {
            Constants.OS = "unknown";
        }

        Constants.ARCH = Arch.getArch().toString();

        // Delete mismatched dlls
        if (config.getAsInt("dllVersion") != Constants.DLL_VERSION) {
            Constants.LOG.info("DLL Version did change, removing old files...");
            DLLHandler.clearDLL();
            config.properties.setProperty("dllVersion", String.valueOf(Constants.DLL_VERSION));
            config.write();
        }

        // Init natives
        if (!onInit()) {
            System.exit(-9515);
        }
    }

    private boolean onInit() {
        deleteOldLog();
        if (!new File(LibraryMapping.libVLC.linuxName).isFile() && !new File(LibraryMapping.libVLC.windowsName).isFile() && !new File(LibraryMapping.libVLC.macName).isFile()) {
            Constants.LOG.info("Unpacking natives...");
            if (!DLLHandler.unpack(this.getClass().getClassLoader())) {
                Constants.LOG.warn("We do not bundle natives for your os. You can try to manually install VLC Player or libVLC for your System. FancyVideo-API only runs with libVLC Versions 4.0.0+");
            }
        }
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "");
        try {
            String path = discoverNativeVLC();
            Constants.LOG.info("Native VLC Found at '{}'", path);
            return true;
        } catch (UnsatisfiedLinkError e1) {
            Constants.LOG.error("Couldn't load vlc binaries, crashing...");
            return false;
        }
    }

    private String discoverNativeVLC() {
        String nativeLibraryPath;
        discovery.discover();
        NativeDiscoveryStrategy nativeDiscoveryStrategy = discovery.successfulStrategy();
        nativeLibraryPath = discovery.discoveredPath();
        Constants.LOG.debug("Strategy: {}", nativeDiscoveryStrategy);
        Constants.LOG.debug("Path: {}", nativeLibraryPath);

        try {
            checkVersion();
        } catch (LinkageError e) {
            throw new NativeLibraryMappingException("Failed to properly initialise the native library", e);
        }
        return nativeLibraryPath;
    }

    private void checkVersion() throws LinkageError {
        LibVlcVersion version = new LibVlcVersion();
        Constants.LOG.debug(String.valueOf(new Version(libvlc_get_version())));
        if (!version.isSupported()) {
            throw new LinkageError(String.format("Failed to find minimum required VLC version %s, found %s", version.getRequiredVersion(), version.getVersion()));
        }
    }

    private static void deleteOldLog() {
        try {
            Files.delete(new File("logs/vlc.log").toPath());
        } catch (NoSuchFileException ignored) {
            // Ignored
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

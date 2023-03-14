/*
 * This file is part of the FancyVideo-API.
 *
 * The FancyVideo-API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The FancyVideo-API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * The FancyVideo-API uses VLCJ, Copyright 2009-2021 Caprica Software Limited,
 * licensed under the GNU General Public License.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * You should have received a copy of the GNU General Public License
 * along with FancyVideo-API.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2022 Nick1st.
 */

package nick1st.fancyvideo; //NOSONAR

import com.sun.jna.NativeLibrary; //NOSONAR
import nick1st.fancyvideo.api.MediaPlayerHandler;
import nick1st.fancyvideo.api.eventbus.EventException;
import nick1st.fancyvideo.api.eventbus.FancyVideoEventBus;
import nick1st.fancyvideo.api.eventbus.event.PlayerRegistryEvent;
import nick1st.fancyvideo.config.SimpleConfig;
import nick1st.fancyvideo.example.APIExample;
import nick1st.fancyvideo.internal.Arch;
import nick1st.fancyvideo.internal.DLLHandler;
import nick1st.fancyvideo.internal.LibraryMapping;
import org.apache.commons.lang3.SystemUtils;
import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;
import uk.co.caprica.vlcj.support.version.LibVlcVersion;
import uk.co.caprica.vlcj.support.version.Version;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import static uk.co.caprica.vlcj.binding.lib.LibVlc.libvlc_get_version;

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

        Constants.LOG.info("Running on OS: {} {}", Constants.OS, Constants.ARCH);

        // Delete mismatched dlls
        if (config.getAsInt("dllVersion") != Constants.DLL_VERSION || Constants.DEBUG_NO_LIBRARY_MODE) {
            Constants.LOG.info("DLL Version did change, removing old files...");
            DLLHandler.clearDLL();
            config.properties.setProperty("dllVersion", String.valueOf(Constants.DLL_VERSION));
            config.write();
        }

        // Init natives
        if (!onInit()) {
            System.exit(-9515); // TODO Run in NO_LIBRARY mode instead of causing a "soft" crash
        }

        Runtime.getRuntime().addShutdownHook(new ShutdownHook());

        // Setup Example?
        if (config.getAsBool("example")) {
            try {
                APIExample example = new APIExample();
                FancyVideoEventBus.getInstance().registerEvent(example);
            } catch (EventException e) {
                Constants.LOG.error("A critical error happened", e);
            }
        }
    }

    /**
     * This should be called as soon as a GL Context is available.
     */
    public void apiSetup() {
        // Setup API
        MediaPlayerHandler.getInstance();
        try {
            FancyVideoEventBus.getInstance().registerEvent(MediaPlayerHandler.getInstance());
            FancyVideoEventBus.getInstance().registerEvent(FancyVideoEvents.class);
        } catch (EventException e) {
            Constants.LOG.error("A critical error happened", e);
        }
        FancyVideoEventBus.getInstance().runEvent(new PlayerRegistryEvent.AddPlayerEvent());
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
            Constants.LOG.error("Couldn't load vlc binaries, loading in NO_LIBRARY mode...");
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
            Constants.LOG.error("Failed to properly initialise the native library");
            Constants.LOG.trace("Stacktrace:", e);
            Constants.NO_LIBRARY_MODE = true;
        }
        return nativeLibraryPath;
    }

    private void checkVersion() throws LinkageError {
        LibVlcVersion version = new LibVlcVersion();
        if (Constants.LOG.isDebugEnabled()) {
            Constants.LOG.debug(String.valueOf(new Version(libvlc_get_version())));
        }
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

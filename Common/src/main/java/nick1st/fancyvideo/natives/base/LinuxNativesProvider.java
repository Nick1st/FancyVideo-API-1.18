package nick1st.fancyvideo.natives.base;

import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.internal.Arch;
import nick1st.fancyvideo.natives.api.AvailableNativeListProvider;
import nick1st.fancyvideo.natives.api.NativeGroup;
import nick1st.fancyvideo.natives.api.NativeListEntry;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class LinuxNativesProvider implements AvailableNativeListProvider {
    @Override
    public boolean isListValid(int dllVersion) {
        if (dllVersion == Constants.DLL_VERSION) {
            if (SystemUtils.IS_OS_LINUX) {
                return Arch.getArch() == Arch.amd64 || Arch.getArch() == Arch.x86;
            }
        }
        return false;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Nullable
    @Override
    public NativeListEntry[] getModules() {
        return NativeGroup.BASE.getModules().toArray(new NativeListEntry[0]);
    }

    public static boolean install() {
        try {

            InputStream in = LinuxNativesProvider.class.getClassLoader().getResourceAsStream("vlc-bin/linux/vlc.sh");
            OutputStream out = new FileOutputStream("vlc.sh");
            IOUtils.copy(in, out);
            in.close();
            out.flush();
            out.close();

            Process installScript = Runtime.getRuntime().exec("/bin/bash vlc.sh", new String[0], new File(""));
            installScript.waitFor();
            int status = installScript.exitValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}

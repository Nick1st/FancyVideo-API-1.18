package nick1st.fancyvideo.natives.api;

import nick1st.fancyvideo.Constants;
import org.apache.commons.compress.utils.IOUtils;

import javax.annotation.Nullable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Services implementing this provide a set of modules. <br>
 * You <b>MUST</b> implement {@link AvailableNativeListProvider#isListValid(int dllVersion)}.
 * @since 3.0.0
 */
public interface AvailableNativeListProvider extends NativeListProviders{

    /**
     * Normally the return value of this methode should be dependent on <br>
     * 1. dllVersion. <br>
     * 2. OS and Arch. <br>
     * 3. Other module requirements. <br>
     * <p>
     * It's up to the implementation to fulfill this requirement. <br>
     * Having a look at {@link org.apache.commons.lang3.SystemUtils} might help.
     * <p>
     * <b>THIS MUST BE IMPLEMENTED, OTHERWISE THE LIST WON'T LOAD.</b>
     *
     * @param dllVersion The dllVersion FancyVideo-API was compiled against.
     * @return true if this list is valid in its current environment, false otherwise.
     * @since 3.0.0
     */
    default boolean isListValid(int dllVersion){
        return false;
    }

    /**
     *
     * @return True if this service provides an own native installation mechanism, false otherwise.
     * @since 3.0.0
     */
    default boolean isSpecial() {
        return false;
    }

    /**
     * @return The modules provided by this service.
     * @since 3.0.0
     */
    @Override
    @Nullable
    NativeListEntry[] getModules();

    /**
     * @return A list of {@link NativeGroup}s provided by this service.
     * @since 3.0.0
     */
    @Override
    @Nullable
    NativeGroup[] getModuleGroups();

    /**
     * If this service provides a custom installation procedure it is launched by calling
     * this method. All custom installation logic must run before this method returns.
     *
     * @return True if the custom installation was successful, false otherwise.
     * @since 3.0.0
     */
    default boolean install() {
        return false;
    }

    /**
     * Logic to install a single named module. <br>
     * The default implementation is normally enough to install a module on Windows.
     * @param module The module to install.
     * @return True if installation was successful, false otherwise.
     * @since 3.0.0
     */
    default boolean installModule(NativeListEntry module) {
        try {
            InputStream in = this.getClass().getClassLoader().getResourceAsStream(Constants.OS + "/" + Constants.ARCH + "/" + module.getPath().getPath() + ".dll");
            module.getPath().getParentFile().mkdirs();
            OutputStream out = new FileOutputStream(module.getPath() + ".dll");
            IOUtils.copy(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (IOException e) {
            Constants.LOG.error("An error occurred whilst trying to unpack native ", e);
            return false;
        }
    }
}

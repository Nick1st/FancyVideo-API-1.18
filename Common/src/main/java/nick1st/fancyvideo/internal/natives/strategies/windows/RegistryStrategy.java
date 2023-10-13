package nick1st.fancyvideo.internal.natives.strategies.windows;

import nick1st.fancyvideo.internal.natives.WindowsDiscovery;
import org.apache.logging.log4j.core.util.Integers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RegistryStrategy implements WindowsDiscovery.Strategy {

    public static void main(String[] args) {
        new RegistryStrategy().discover();
    }

    @Override
    public Map<Path, Boolean> discover() {
        Map<Path, Boolean> temp = new HashMap<>();
        try {
            Process ps = Runtime.getRuntime().exec("powershell.exe -command \"Get-ItemProperty HKLM:\\Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\* | Select-Object DisplayName, InstallLocation, VersionMajor | where {$_.DisplayName -match \"\"\"VLC\"\"\"} | Format-List\""); //VLC
            BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            String s;
            System.out.println("Output");
            Program p = null;
            while ((s = reader.readLine()) != null) {
                if (!s.equals("")) {
                    if (s.startsWith("InstallLocation")) {
                        p = new Program();
                        p.InstallLocation = s.strip().split(":", 2)[1].strip();
                    } else if (s.startsWith("VersionMajor")) {
                        assert p != null; //p is never going to be null. This is because of the way the output from the PS query is.
                        p.VersionMajor = Integers.parseInt(s.strip().split(":", 2)[1].strip());
                        System.out.println(p);
                        p.addToMap(temp);
                    }
                    //System.out.println(s);
                }
            }
            // TODO Check if all required Files exist
            System.out.println("Finished");
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    static class Program {
        public String InstallLocation;
        public int VersionMajor;

        public void addToMap(Map<Path, Boolean> m) {
            m.putIfAbsent(new File(InstallLocation).toPath().toAbsolutePath(), VersionMajor == 4);
        }

        @Override
        public String toString() {
            return "Program{" +
                    "InstallLocation='" + InstallLocation + '\'' +
                    ", VersionMajor=" + VersionMajor +
                    '}';
        }
    }
}

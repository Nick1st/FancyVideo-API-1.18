package nick1st.fancyvideo.internal;

import com.sun.jna.Platform;

public enum Arch {
    x86,
    amd64,
    arm,
    arm64,
    strange,
    unknown;

    private static Arch arch;

    public static Arch getArch() {
        if (arch == null) {
            switch (Platform.ARCH) {
                case ("x86") -> arch = x86;
                case ("amd64"), ("x86-64") -> arch = amd64;
                case ("ppc"), ("ppc64"), ("ppc64le"), ("ia64"), ("sparcv9"), ("mips64"), ("mips64el") -> arch = strange;
                case ("arm"), ("armel") -> arch = arm;
                case ("arm64") -> arch = arm64;
                default -> arch = unknown;
            }
        }
        return arch;
    }
}

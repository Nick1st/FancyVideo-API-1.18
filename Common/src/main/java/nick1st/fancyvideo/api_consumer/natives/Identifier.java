package nick1st.fancyvideo.api_consumer.natives;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Identifier implements Comparable {
    private static int latestModuleId = 0;
    private static int latestGroupId = 0;
    private static int latestHolderId = 0;

    private final int id;
    private final Type type;

    Identifier(Type type) {
        this.type = type;
        switch (type) {
            case MODULE -> this.id = latestModuleId++;
            case GROUP -> this.id = latestGroupId++;
            case HOLDER -> this.id = latestHolderId++;
            default -> this.id = -1;
        }
    }

    public boolean isHolder() {
        return type == Type.HOLDER;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return id == that.id && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return String.valueOf(type.toString().charAt(0)) + id;
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o instanceof Identifier other) {
            if (this.type == other.type) {
                return Integer.compare(this.id, other.id);
            } else {
                return type.compareTo(other.type);
            }
        }
        throw new IllegalArgumentException("Identifier#compareTo() expects an Identifier for comparison, but received another type.");
    }

    enum Type {
        MODULE,
        GROUP,
        HOLDER,
        INVALID
    }
}

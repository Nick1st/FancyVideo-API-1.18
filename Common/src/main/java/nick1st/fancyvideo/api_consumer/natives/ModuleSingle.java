package nick1st.fancyvideo.api_consumer.natives;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A ModuleSingle maps a module to a {@link ModulePOJO}.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class ModuleSingle implements ModuleLike {
    private final ModulePOJO modulePOJO;
    private final int requestCount;
    private final ResourceLocation identifier;

    /**
     * Internal constructor to create non-mutable ModuleSingle instances.
     * @param identifier The identifier for this ModuleSingle.
     * @param requestCount How often this ModuleSingle was marked as a feature.
     * @since 3.0.0
     */
    ModuleSingle(@Nonnull ResourceLocation identifier, int requestCount) {
        if (!identifier.getNamespace().equals("vlc_modules")) {
            throw new IllegalArgumentException("Illegal ModuleSingle identifier. ModuleSingle identifier namespace must be 'vlc_modules'.");
        }
        this.modulePOJO = new ModulePOJO(identifier.getPath());
        this.identifier = identifier;
        this.requestCount = requestCount;
    }

    /**
     * @return The identifier of this ModuleSingle.
     * @since 3.0.0
     */
    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public String toString() {
        return modulePOJO.getModuleIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleSingle that = (ModuleSingle) o;
        return getIdentifier().equals(that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}

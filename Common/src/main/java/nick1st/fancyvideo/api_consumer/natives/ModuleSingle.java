package nick1st.fancyvideo.api_consumer.natives;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * A ModuleSingle maps a module to a {@link ModulePOJO}.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public final class ModuleSingle implements ModuleLike {
    private final ModulePOJO modulePOJO;

    final ResourceLocation identifier;


    /**
     * Constructor to create ModuleSingle instances.
     * @param identifier The identifier for this ModuleSingle.
     * @since 3.0.0
     */
    public ModuleSingle(@Nonnull ResourceLocation identifier) {
        if (!identifier.getNamespace().equals("vlc_modules")) {
            throw new IllegalArgumentException("Illegal ModuleSingle identifier. ModuleSingle identifier namespace must be 'vlc_modules'.");
        }
        this.modulePOJO = new ModulePOJO(identifier.getPath());
        this.identifier = identifier;
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
        if (o instanceof ModuleHolder holder) { // This allows matching a holder
            return getIdentifier().equals(holder.placeholderFor);
        }
        if (!(o instanceof ModuleSingle that)) return false; // Note that this does allow subclasses
        return getIdentifier().equals(that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}

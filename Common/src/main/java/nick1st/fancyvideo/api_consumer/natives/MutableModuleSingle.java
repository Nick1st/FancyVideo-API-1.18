package nick1st.fancyvideo.api_consumer.natives;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * A mutable {@link ModuleSingle}.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class MutableModuleSingle implements ModuleLike{
    boolean isFeature;
    final ResourceLocation identifier;
    final ModulePOJO modulePOJO;

    /**
     * Creates a ModularModuleSingle.
     * @param identifier The {@link ResourceLocation} used as a unique identifier. The namespace needs to be
     *                   'vlc_modules'.
     * @since 3.0.0
     */
    public MutableModuleSingle(ResourceLocation identifier) {
        if (!identifier.getNamespace().equals("vlc_modules")) {
            throw new IllegalArgumentException("Illegal ModuleSingle identifier. ModuleSingle identifier namespace must be 'vlc_modules'.");
        }
        this.modulePOJO = new ModulePOJO(identifier.getPath());
        this.identifier = identifier;
    }

    /**
     * Factory method used to toggle if this ModuleSingle counts as a feature.
     * @param isFeature If this Module is a feature.
     * @return The MutableModuleSingle itself.
     * @since 3.0.0
     */
    public MutableModuleSingle isFeature(boolean isFeature) {
        this.isFeature = isFeature;
        return this;
    }

    /**
     * Builds the MutableModuleSingle and registers it to the registry. After that you should drop the reference to the
     * MutableModuleSingle and never modify it again.
     * @return The identifier of this ModuleSingle. Use {@link ModuleLike.Registry#getModuleSingle(ResourceLocation)} to
     *         get a reference to the unmodifiable ModuleSingle.
     * @since 3.0.0
     */
    public ResourceLocation build() {
        ModuleLike.Registry.tryAddingModule(this);
        return identifier;
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
        return "MutableModuleSingle{" +
                "identifier=" + identifier +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableModuleSingle that = (MutableModuleSingle) o;
        return getIdentifier().equals(that.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifier());
    }
}

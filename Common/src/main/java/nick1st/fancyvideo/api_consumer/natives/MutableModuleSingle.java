package nick1st.fancyvideo.api_consumer.natives;

import net.minecraft.resources.ResourceLocation;

/**
 * A mutable {@link ModuleSingle}.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class MutableModuleSingle extends ModuleSingle implements ModuleLike{
    boolean isFeature;
    private boolean isAlreadyBuild;

    /**
     * Creates a MutableModuleSingle.
     * @param identifier The {@link ResourceLocation} used as a unique identifier. The namespace needs to be
     *                   'vlc_modules'.
     * @since 3.0.0
     */
    public MutableModuleSingle(ResourceLocation identifier) {
        super(identifier, 0);
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
     * Builds the MutableModuleSingle and registers it to the registry. After that you should drop all references to the
     * MutableModuleSingle and never modify it again.
     * @return The identifier of this ModuleSingle. Use {@link ModuleLike.Registry#getModuleSingle(ResourceLocation)} to
     *         get a reference to the unmodifiable ModuleSingle.
     * @since 3.0.0
     */
    public ResourceLocation build() {
        if (isAlreadyBuild) {
            throw new UnsupportedOperationException("MutableModuleSingle already build.");
        }
        ModuleLike.Registry.tryAddingModule(this);
        isAlreadyBuild = true;
        return identifier;
    }

    @Override
    public String toString() {
        return "MutableModuleSingle{" +
                "identifier=" + identifier +
                '}';
    }
}

package nick1st.fancyvideo.api_consumer.natives;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * This class can be used to act as a placeholder for a {@link ModuleGroup} or a {@link ModuleSingle}.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class ModuleHolder implements ModuleLike{

    private final ResourceLocation internalIdentifier;
    final ResourceLocation placeholderFor;
    boolean isFeature;

    int featureCount;

    /**
     * Public constructor to initialise the holder.
     * @param internalIdentifier The identifier this is a placeholder for.
     * @param isFeature If this ModuleHolder should add a feature mark to the resolved {@link ModuleLike}.
     * @since 3.0.0
     */
    public ModuleHolder(ResourceLocation internalIdentifier) { // TODO this really should be a holder for only groups, as ModuleSingle are instantly creatable from a Holder, so there really is no point in them being able to be a holder
        this.internalIdentifier = new ResourceLocation("placeholder_" + internalIdentifier.getNamespace(), internalIdentifier.getPath());
        this.placeholderFor = internalIdentifier;
        this.isFeature = false; // TODO Remove this, this is an outdated way
    }

    /**
     * Private Constructor for internal creation.
     * @param internalIdentifier The {@link ResourceLocation} used as a unique internal identifier.
     * @param requestCount How often this holder alias was requested.
     * @since 3.0.0
     */
    ModuleHolder(ResourceLocation internalIdentifier, int requestCount) {
        this.internalIdentifier = internalIdentifier;
        this.placeholderFor = new ResourceLocation(this.internalIdentifier.toString().substring(12));
        this.featureCount = requestCount;
    }

    /**
     * This will register the Holder to the registry. All ModuleGroups/ModuleSingles need
     * to be registered to the Registry before it finishes loading, so that all Holders can be resolved.
     * @return The ResourceLocation that is internally used to describe this Holder.
     * @since 3.0.0
     */
    public ResourceLocation build() {
        ModuleLike.Registry.tryAddingHolder(this);
        return internalIdentifier;
    }

    /**
     * @return The identifier of the ModuleLike this is a placeholder for.
     * @since 3.0.0
     */
    public ResourceLocation getInternalIdentifier() {
        return internalIdentifier;
    }

    @Override
    public String toString() {
        return "ModuleHolder{" +
                "identifierPH=" + placeholderFor +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleHolder that = (ModuleHolder) o;
        return getInternalIdentifier().equals(that.getInternalIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInternalIdentifier());
    }

    @Override
    public ResourceLocation getIdentifier() {
        return placeholderFor;
    }
}

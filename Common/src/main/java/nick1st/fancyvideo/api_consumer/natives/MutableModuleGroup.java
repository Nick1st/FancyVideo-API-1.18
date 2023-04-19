package nick1st.fancyvideo.api_consumer.natives;

import com.google.common.graph.MutableValueGraph;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A mutable {@link ModuleGroup}.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public final class MutableModuleGroup implements ModuleLike {
    final Set<ModuleLike> containedModules = new HashSet<>();
    boolean isFeature;
    final ResourceLocation identifier;

    /**
     * Creates a ModularModuleGroup.
     * @param identifier The {@link ResourceLocation} used as a unique identifier.
     * @since 3.0.0
     */
    public MutableModuleGroup(@Nonnull ResourceLocation identifier) {
        this.identifier = identifier;
    }

    /**
     * Factory method to add a contained {@link ModuleLike}.
     * @deprecated Use {@link #contain(ResourceLocation)} instead.
     * @param dependency The ModuleLike that should be contained in this group.
     * @return The MutableModuleGroup itself.
     * @since 3.0.0
     */
    @Deprecated(since = "3.0.0")
    public MutableModuleGroup contain(@Nonnull ModuleLike dependency) {
        containedModules.add(dependency);
        return this;
    }

    /**
     * Factory method to add contained {@link ModuleLike}s.
     * @param dependencies The ModuleLikes that should be contained in this group.
     * @return The MutableModuleGroup itself.
     * @since 3.0.0
     */
    public MutableModuleGroup containAll(@Nonnull Set<?> dependencies) {
        if (dependencies.iterator().next() instanceof ModuleLike) {
            dependencies.forEach(dependency -> containedModules.add((ModuleLike) dependency));
            Constants.LOG.warn("Using type ModuleLike in a Builder is deprecated since 3.0.0");
        } else if (dependencies.iterator().next() instanceof ResourceLocation) {
            dependencies.forEach(dependency -> containedModules.add(ModuleLike.Registry.getModuleGroup((ResourceLocation) dependency)));
        } else {
            throw new IllegalArgumentException("Provided Set not of type ModuleLike (deprecated) or ResourceLocation.");
        }
        return this;
    }

    /**
     * Factory method to add a contained {@link ModuleLike}.
     * @param dependency A ResourceLocation of the ModuleLike that should be contained in this group.
     * @return The MutableModuleGroup itself.
     * @since 3.0.0
     */
    public MutableModuleGroup contain(@Nonnull ResourceLocation dependency) {
        if ("vlc_modules".equals(dependency.getNamespace())) {
            containedModules.add(ModuleLike.Registry.getModuleSingle(dependency));
        } else if (dependency.getNamespace().startsWith("placeholder_")) {
            containedModules.add(ModuleLike.Registry.getModuleHolderByInternalId(dependency));
        } else {
            containedModules.add(ModuleLike.Registry.getModuleGroup(dependency));
        }
        return this;
    }

    /**
     * Factory method used to toggle if this ModuleGroup counts as a feature.
     * @param isFeature If this ModuleGroup is a feature.
     * @return The MutableModuleGroup itself.
     * @since 3.0.0
     * @deprecated
     */
    @Deprecated(forRemoval = true)
    public MutableModuleGroup isFeature(boolean isFeature) {
        this.isFeature = isFeature;
        return this;
    }

    /**
     * Builds the MutableModuleGroup and registers it to the registry. After that you should drop the reference to the
     * MutableModuleGroup and never modify it again.
     * @return The identifier of this ModuleGroup. Use {@link ModuleLike.Registry#getModuleGroup(ResourceLocation)} to
     *         get a reference to the unmodifiable ModuleGroup.
     * @since 3.0.0
     */
    public ResourceLocation build() { // TODO This should build all contained MutableModuleGroups
        ModuleLikeRegistry.addModuleLikeOfAnyKind(this);
        ModuleLike.Registry.tryAddingGroup(this);
        return identifier;
    }

    /**
     * @return The identifier of this ModuleGroup.
     * @since 3.0.0
     */
    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MutableModuleGroup that = (MutableModuleGroup) o;
        return containedModules.equals(that.containedModules);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containedModules);
    }

    @Override
    public String toString() {
        return "MutableModuleGroup{" +
                "identifier=" + identifier +
                '}';
    }
}

package nick1st.fancyvideo.api_consumer.natives;

import com.google.common.graph.MutableValueGraph;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A ModuleGroup defines a group of modules that should stick together during environment setup.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class ModuleGroup implements ModuleLike {
    public ModuleSingle mainModule;
    //private final Set<ModuleLike> containedModules = new HashSet<>(); // Unused
    final Set<Integer> containedModulesId = new HashSet<>();
    private final int requestCount;
    private final ResourceLocation identifier;

//    @Deprecated
//    public ModuleGroup(ModuleSingle mainNode) {
//        graph.addNode(mainNode);
//        this.mainNode = mainNode;
//        name = "";
//    }

    /**
     * Private Constructor for internal creation. Use the public one ({@link MutableModuleGroup#MutableModuleGroup(ResourceLocation)}) instead.
     * @param identifier The {@link ResourceLocation} used as a unique identifier.
     * @param containedModulesId The modules contained in this group.
     * @param requestCount How often this group was requested.
     * @since 3.0.0
     */
    ModuleGroup(@Nonnull ResourceLocation identifier, @Nonnull Set<Integer> containedModulesId, int requestCount) {
        this.identifier = identifier;
        this.containedModulesId.addAll(containedModulesId);
        this.requestCount = requestCount;
    }

    /**
     * @return the contained modules
     * @since 3.0.0
     */
    Set<Integer> getContainedModules() {
        return containedModulesId;
    }

    /**
     * @return The identifier of this ModuleGroup.
     * @since 3.0.0
     */
    @Override
    public ResourceLocation getIdentifier() {
        return identifier;
    }

    /**
     * @return How often this ModuleGroup was requested.
     * @since 3.0.0
     */
    int getRequestCount() {
        return requestCount;
    }

//    private final MutableValueGraph<Object, Object> graph = ValueGraphBuilder.directed().allowsSelfLoops(false).nodeOrder(ElementOrder.insertion()).build();
//    private final ModuleSingle mainNode;

//    public ModuleGroup depends(ModuleLike dependency) {
//        graph.addNode(dependency);
//
//        graph.putEdgeValue(dependency, mainNode, 0);
//    }

//    public ModuleLike _depends(MutableValueGraph<ModuleLike, Integer> graph, ModuleLike topModule) {
//        // Get how many feature marks the bottom node of this connection has
//        Integer featureValue = graph.edgeValue(mainNode, topModule).orElse(0);
//        graph.putEdgeValue(mainNode, topModule, featureValue + requestCount);
//
//        // recursively build graph from bottom to top
//        for (ModuleLike dependency : containedModules) {
//            dependency._depends(graph, mainModule);
//        }
//
//        // Check if the module we are going to add has already been marked as a feature
//        Optional<Integer> featureCount = topLevelGraph.edgeConnecting(mainNode, topModule);
//        topLevelGraph.addEdge()
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleGroup that = (ModuleGroup) o;
        return containedModulesId.equals(that.containedModulesId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containedModulesId);
    }

    @Override
    public String toString() {
        return "ModuleGroup{" +
                "identifier=" + identifier +
                '}';
    }
}

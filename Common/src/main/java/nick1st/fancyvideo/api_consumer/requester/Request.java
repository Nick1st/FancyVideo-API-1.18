package nick1st.fancyvideo.api_consumer.requester;

import com.google.common.graph.MutableNetwork;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.NetworkBuilder;
import com.google.common.graph.ValueGraphBuilder;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.api_consumer.natives.ModuleGroup;
import nick1st.fancyvideo.api_consumer.natives.ModuleHolder;
import nick1st.fancyvideo.api_consumer.natives.ModuleLike;
import nick1st.fancyvideo.api_consumer.natives.ModuleSingle;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class features an internal request registry class, as well as a {@link #New(ResourceLocation)} method. A
 * Request needs to be registered in order to be satisfied. Registration happens in the
 * {@link #New(ResourceLocation)} method. Building is done later by the library.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public class Request {

    static Set<Request> allRequests = new HashSet<>();

    private final ResourceLocation requested;

    /**
     * Private constructor for internal use only. Use the {@link #New(ResourceLocation)} method instead.
     * @since 3.0.0
     */
    private Request(ResourceLocation requestedModuleLike) {
        this.requested = requestedModuleLike;
    }

    /**
     * Public builder method for a Request.
     * @param requestedModuleLike The Identifier of a {@link ModuleLike} that should be registered.
     * @since 3.0.0
     */
    public static void New(ResourceLocation requestedModuleLike) {
        Request _this = new Request(requestedModuleLike);
        allRequests.add(_this);
    }



    public static class ModuleRequestGraph { // TODO Compile ModuleHolders before this
        static final Integer ROOT_NODE = 0;
        // Integer: Id | Integer: RequestCount
        static MutableValueGraph<Integer, Integer> graph = ValueGraphBuilder.directed().build();
        static {
            graph.addNode(ROOT_NODE); // This is the root node.
        }

        public static void buildAllRequests() {
            Request.allRequests.forEach(ModuleRequestGraph::buildRequest);
        }

        private static void buildRequest(Request request) {
            ResourceLocation thisRequestsMainNode = request.requested;
            Integer mainNode = ModuleLike.Registry.identifiersToId.get(thisRequestsMainNode);
            recursivelyCompleteGraph(ROOT_NODE, mainNode);
        }

        private static void recursivelyCompleteGraph(Integer rootNode, Integer node) {
            Set<ResourceLocation> topLocation = ModuleLike.Registry.knownModuleLikeIdentifiers.get(node);
            if (topLocation == null || topLocation.isEmpty()) {
                throw new RuntimeException("Somehow got Node without known Identifiers.");
            }
            if (((ResourceLocation) topLocation.toArray()[0]).getNamespace().equals("vlc_modules")) {
                // We got a ModuleSingle
                graph.putEdgeValue(node, rootNode, 0);
                graph.putEdgeValue(node, ROOT_NODE, ModuleLike.Registry.featureCount.get(node));
            } else if (((ResourceLocation) topLocation.toArray()[0]).getNamespace().startsWith("placeholder_")) {
                // We got a ModuleHolder
                throw new RuntimeException("Somehow got an unresolved ModuleHolder.");
            } else if (((ResourceLocation) topLocation.toArray()[0]).getPath().startsWith("groups/")) {
                // We got a ModuleGroup
                graph.putEdgeValue(node, rootNode, 0);
                graph.putEdgeValue(node, ROOT_NODE, ModuleLike.Registry.featureCount.get(node));
                ModuleLike.Registry.contains.get(node).forEach(moduleLike -> recursivelyCompleteGraph(node, moduleLike));
            } else {
                throw new RuntimeException("Somehow got an unidentified ModuleLike.");
            }
        }

        public static void compileModuleLikeRegistry() {
            ModuleLike.Registry.holderNotMapped.forEach(notMapped -> {
                Integer idToMapTo = ModuleLike.Registry.identifiersToId.get(new ResourceLocation(notMapped.toString().substring(12)));
                if (idToMapTo == null) {
                    throw new RuntimeException("A Holder can not be mapped, although registration is finished");
                }
                ModuleLike.Registry.holderMapping.put(ModuleLike.Registry.identifiersToId.get(
                        new ResourceLocation("placeholder_" + notMapped.toString())), idToMapTo);
            });
            ModuleLike.Registry.holderNotMapped.retainAll(new ArrayList<ResourceLocation>());
            ModuleLike.Registry.holderMapping.keySet().forEach(mappedFrom -> {
                Integer mappedTo = ModuleLike.Registry.holderMapping.get(mappedFrom);
                // Get all ModuleGroups that this is contained in
                ModuleLike.Registry.containedIn.get(mappedFrom).forEach(contained -> {
                    ModuleLike.Registry.contains.get(contained).remove(mappedFrom);
                    ModuleLike.Registry.contains.get(contained).add(mappedTo);
                    int thisFeatureCount = ModuleLike.Registry.featureCount.get(mappedFrom);
                    int previousFeatureCount = ModuleLike.Registry.featureCount.get(mappedTo);
                    ModuleLike.Registry.featureCount.put(mappedTo, thisFeatureCount + previousFeatureCount);
                    ModuleLike.Registry.featureCount.put(mappedFrom, 0);
                });
                ModuleLike.Registry.containedIn.get(mappedTo).addAll(ModuleLike.Registry.containedIn.get(mappedFrom));
                ModuleLike.Registry.containedIn.remove(mappedFrom);
            });

            // Now check and fix different named holders creating different registry entries
            boolean registryWasChanged = true;

            while (registryWasChanged) {
                registryWasChanged = rerunEqualityChecks();
            }
        }

        private static boolean rerunEqualityChecks() {
            Integer[] groupDefinitionsKeys = ModuleLike.Registry.contains.keySet().toArray(new Integer[0]);
            Set<Integer>[] groupDefinitionsValues = ModuleLike.Registry.contains.values().toArray(new Set[0]);
            // Integer: Duplicate | Integer: MapToThis
            Map<Integer, Integer> remap = new HashMap<>();
            for (int i = 0; i < groupDefinitionsKeys.length; i++) {
                Set<Integer> currentSet = groupDefinitionsValues[i];
                for (int j = i + 1; j < groupDefinitionsValues.length; j++) {
                    if (remap.containsKey(groupDefinitionsKeys[j])) {
                        // This is already contained as a Duplicate in the map, meaning this will already be mapped to something else
                    } else {
                        if (currentSet.equals(groupDefinitionsValues[j])) {
                            remap.put(groupDefinitionsKeys[j], groupDefinitionsKeys[i]);
                        }
                    }
                }
            }

            remap.forEach((duplicate, mapToThis) -> {
                ModuleLike.Registry.contains.remove(duplicate);
                ModuleLike.Registry.holderMapping.replaceAll((key, value) -> Objects.equals(value, duplicate) ? mapToThis : value);
                ModuleLike.Registry.identifiersToId.replaceAll((key, value) -> Objects.equals(value, duplicate) ? mapToThis : value);

                Integer duplicateFeatureCount = ModuleLike.Registry.featureCount.get(duplicate);
                ModuleLike.Registry.featureCount.remove(duplicate);
                Integer mapToFeatureCount = ModuleLike.Registry.featureCount.get(mapToThis);
                ModuleLike.Registry.featureCount.put(mapToThis, duplicateFeatureCount + mapToFeatureCount);
                Set<Integer> duplicateContainedIn = ModuleLike.Registry.containedIn.get(duplicate);
                duplicateContainedIn.forEach(duplicateContainedInGroup -> {
                    ModuleLike.Registry.contains.get(duplicateContainedInGroup).remove(duplicate);
                    ModuleLike.Registry.contains.get(duplicateContainedInGroup).add(mapToThis);
                });
                ModuleLike.Registry.containedIn.remove(duplicate);

                Set<ResourceLocation> duplicateKnowIdentifiers = ModuleLike.Registry.knownModuleLikeIdentifiers.get(duplicate);
                ModuleLike.Registry.knownModuleLikeIdentifiers.remove(duplicate);
                ModuleLike.Registry.knownModuleLikeIdentifiers.get(mapToThis).addAll(duplicateKnowIdentifiers);
            });

            return !remap.isEmpty();
        }
    }
}

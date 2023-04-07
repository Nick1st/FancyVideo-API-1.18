package nick1st.fancyvideo.api_consumer;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;
import nick1st.fancyvideo.api.eventbus.EventException;
import nick1st.fancyvideo.api.eventbus.FancyVideoEventBus;
import nick1st.fancyvideo.api.eventbus.event.EnvironmentSetupEvent;
import nick1st.fancyvideo.api_consumer.natives.ModuleLike;
import nick1st.fancyvideo.api_consumer.requester.Request;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is responsible for handling ApiConsumers during API-Initialisation (similar to the FML Loading Bus).
 * @since 3.0.0
 */
public class ConsumerHandler {

    public static void main(String[] args) {
        init();
    }

    /**
     * Initialisation logic
     * @since 3.0.0
     */
    static void init() {
        Constants.LOG.info("Starting ApiConsumer loading");
        Set<ApiConsumer> apiConsumers = ServiceLoader.load(ApiConsumer.class).stream().map(serviceLoader -> serviceLoader.get()).collect(Collectors.toSet());

        Constants.LOG.info("Found the following ApiConsumers:");
        apiConsumers.forEach(consumer -> Constants.LOG.info("   -> " + consumer.getConsumerId()));

        Constants.LOG.trace("Registering Loading-Events");
        List<Class<?>[]> eventClasses = apiConsumers.stream().map(ApiConsumer::registerEvents).toList();
        eventClasses.forEach(classes -> Arrays.stream(classes).forEach(clazz -> {
            try {
                FancyVideoEventBus.getInstance().registerEvent(clazz);
            } catch (EventException.EventRegistryException | EventException.UnauthorizedRegistryException e) {
                Constants.LOG.error("An error occurred during registration of Events in the ApiConsumer Loading Stage", e);
            }
        }));

        Constants.LOG.trace("Try to get some idea of vlc dependency structure / Getting ModuleGroups");
        FancyVideoEventBus.getInstance().runEvent(new EnvironmentSetupEvent.ModuleGroups()); // Meant for Intermod-Comms

        Constants.LOG.trace("Got ModuleGroups. All further Holders need to be instantiated AFTER their group was build"); // TODO Harden the implementation by checking for this condition
        RequestRegistry.ModuleRequestGraph.compileModuleLikeRegistry();

        Constants.LOG.trace("Register Requests");
        FancyVideoEventBus.getInstance().runEvent(new EnvironmentSetupEvent.RegisterRequests(RequestRegistry.REQUEST_REGISTRY));
        RequestRegistry.ModuleRequestGraph.buildAllRequests(); // Here we got our graph. Half-way done. Now only the provider side is left...

    }

    public static class RequestRegistry {
        private static final RequestRegistry REQUEST_REGISTRY = new RequestRegistry();
        private static final MutableValueGraph<Integer, Integer> graph = ValueGraphBuilder.directed().build();

        private final Set<Request> registry = new HashSet<>();

        public void add(Request request) {
            registry.add(request);
        }

        public void addAll(Request... requests) {
            registry.addAll(List.of(requests));
        }




        public static class ModuleRequestGraph { // TODO Compile ModuleHolders before this (kind of done)
            static final Integer ROOT_NODE = 0;
            // Integer: Id | Integer: RequestCount

            static {
                graph.addNode(ROOT_NODE); // This is the root node.
            }

            public static void buildAllRequests() {
                REQUEST_REGISTRY.registry.forEach(ModuleRequestGraph::buildRequest);
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


            // TODO That stuff below should be moved to the ModuleLike Registry one day
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
                    Set<Integer> containedInRemapThing = ModuleLike.Registry.contains.get(duplicate);
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
                    if (!containedInRemapThing.isEmpty()) {
                        // We need to remove this id from the values from contained in
                        containedInRemapThing.forEach(containedDuplicate -> ModuleLike.Registry.containedIn.get(containedDuplicate).remove(duplicate));
                    }

                    Set<ResourceLocation> duplicateKnowIdentifiers = ModuleLike.Registry.knownModuleLikeIdentifiers.get(duplicate);
                    ModuleLike.Registry.knownModuleLikeIdentifiers.remove(duplicate);
                    ModuleLike.Registry.knownModuleLikeIdentifiers.get(mapToThis).addAll(duplicateKnowIdentifiers);
                });

                return !remap.isEmpty();
            }
        }
    }
}

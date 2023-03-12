package nick1st.fancyvideo.api_consumer.natives;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This interface marks a class to behave like a single module in some ways.
 * @since 3.0.0
 * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
 */
public interface ModuleLike {

    ResourceLocation getIdentifier();

    /**
     * The Registry holds known ModuleLikes and their aliases. It has methods to resolve them. It is mostly used
     * for internal purposes. API-Consumers can use aliases in nearly all places.
     * @since 3.0.0
     * @author Nick1st - <a href="mailto:nick1st.dev@gmail.com">{@literal <nick1st.dev@gmail.com>}</a>
     */
    class Registry { // TODO Harden access to the static class fields...
        // Set<ResourceLocation>: HoldersThatAreNotYetMapped // This is actually the identifier it maps to.
        public static final Set<ResourceLocation> holderNotMapped = new HashSet<>();
        // Integer: Id | Integer: MappedTo
        public static final Map<Integer, Integer> holderMapping = new HashMap<>();
        // Integer: Id | Set<ModuleLike>: ContainedModuleLikes
        public static final BiMap<Integer, Set<ModuleLike>> groupDefinitions = HashBiMap.create();
        public static final BiMap<Integer, Set<Integer>> contains = HashBiMap.create();
        // ResourceLocation: Identifier | Integer: Id
        public static final Map<ResourceLocation, Integer> identifiersToId = new HashMap<>();
        // Integer: Id | Set<ResourceLocation>: KnownIdentifiers
        public static final Map<Integer, Set<ResourceLocation>> knownModuleLikeIdentifiers = new HashMap<>();
        // Integer: Id | Integer: FeatureCount
        public static final Map<Integer, Integer> featureCount = new HashMap<>();
        // Integer: Id | Set<Integer>: ContainedInId
        public static final Map<Integer, Set<Integer>> containedIn = new HashMap<>();
        private static int moduleSingleId = -1;
        private static int moduleGroupId = 1;

        /**
         * Private Constructor to hide the public one.
         * @since 3.0.0
         */
        private Registry() {

        }

        /**
         * Internal method to add a ModuleGroup to the registry.
         * @param moduleGroup The ModuleGroup to add.
         * @since 3.0.0
         */
        protected static void tryAddingGroup(MutableModuleGroup moduleGroup) {
            //Integer alreadyExistingId = groupDefinitions.inverse().get(moduleGroup.containedModules); //FIXME Unused
            Integer nameExistingId = identifiersToId.get(moduleGroup.identifier);

            Integer alreadyExistingId = contains.inverse().get(moduleGroup.containedModules.stream()
                    .map(moduleLike -> Registry.identifiersToId.get(moduleLike.getIdentifier()))
                    .collect(Collectors.toSet()));

            // Start by checking if this moduleGroup is already specified under a different name.
            if (alreadyExistingId != null) {
                Constants.LOG.info("Found ModuleGroup declaration [id = {}] that is already known by aliases {}. Considering adding as new alias instead.",
                        alreadyExistingId , knownModuleLikeIdentifiers.get(alreadyExistingId).stream().map(ResourceLocation::toString).collect(Collectors.joining()));

                // Check if the name we're trying to add already exists.
                if (nameExistingId != null) {
                    // The name we're trying to add already exists. This is strange and normally shouldn't be the case
                    if (!alreadyExistingId.equals(nameExistingId)) {
                        // The name is already known but as a different ModuleGroup. This is fatal.
                        Constants.LOG.error("Trying to add an alias for a ModuleGroup, however the name is already linked with a different one. " +
                                        "Offended ModuleGroup: [id = {}], aliases: {}. Offending ModuleGroup: {}, aka {}, [id = {}] ",
                                alreadyExistingId,  knownModuleLikeIdentifiers.get(alreadyExistingId).stream().map(ResourceLocation::toString).collect(Collectors.joining()),
                                moduleGroup.identifier, knownModuleLikeIdentifiers.get(nameExistingId).stream().map(ResourceLocation::toString).collect(Collectors.joining()),
                                nameExistingId);
                        throw new RuntimeException("Trying to add an alias for a ModuleGroup, however the name is already linked with a different one.");
                    } else {
                        // Warn about double registration.
                        Constants.LOG.warn("Trying to register already registered alias {} to ModuleGroup [id = {}], aka {}",
                                moduleGroup.identifier, alreadyExistingId, knownModuleLikeIdentifiers.get(alreadyExistingId).stream().map(ResourceLocation::toString).collect(Collectors.joining()));
                        featureCount.put(alreadyExistingId, featureCount.get(alreadyExistingId) + (moduleGroup.isFeature ? 1 : 0));
                    }
                } else {
                    // The alias is not yet registered, so we register it.
                    identifiersToId.put(moduleGroup.identifier, alreadyExistingId);
                    knownModuleLikeIdentifiers.get(alreadyExistingId).add(moduleGroup.identifier);
                    featureCount.put(alreadyExistingId, featureCount.get(alreadyExistingId) + (moduleGroup.isFeature ? 1 : 0));
                }
            } else {
                // The ModuleGroup is not yet specified.
                // Check if the name is known nevertheless. This would be fatal.
                if (nameExistingId != null) {
                    // The name is already known but as a different ModuleGroup. This is fatal.
                    Constants.LOG.error("Trying to add an alias for a ModuleGroup, however the name is already linked with a different one. " +
                                    "Offended ModuleGroup: [id = {}], aliases: {}. Offending ModuleGroup: {}",
                            nameExistingId,  knownModuleLikeIdentifiers.get(nameExistingId).stream().map(ResourceLocation::toString).collect(Collectors.joining()), moduleGroup.identifier);
                    throw new RuntimeException("Trying to add an alias for a ModuleGroup, however the name is already linked with a different one.");
                } else {
                    // Neither the name nor the specification is known. We can safely add it as a new ModuleGroup.
                    Integer newId = moduleGroupId++;
                    //groupDefinitions.put(newId, moduleGroup.containedModules); // FIXME Unused
                    contains.put(newId, moduleGroup.containedModules.stream()
                            .map(moduleLike -> Registry.identifiersToId.get(moduleLike.getIdentifier()))
                            .collect(Collectors.toSet()));
                    identifiersToId.put(moduleGroup.identifier, newId);
                    knownModuleLikeIdentifiers.put(newId, new HashSet<>());
                    knownModuleLikeIdentifiers.get(newId).add(moduleGroup.identifier);
                    featureCount.put(newId, moduleGroup.isFeature ? 1 : 0);
                    containedIn.put(newId, new HashSet<>());
                    moduleGroup.containedModules.forEach(containedModule ->
                            containedIn.get(identifiersToId.get(containedModule.getIdentifier())).add(newId)
                    );
                }
            }

            // Check if there are holder mappings to fill and do so if appropriate
            if (holderNotMapped.contains(moduleGroup.identifier)) {
                holderMapping.put(identifiersToId.get(
                        new ResourceLocation("placeholder_" + moduleGroup.identifier.toString())),
                        identifiersToId.get(moduleGroup.identifier));
                holderNotMapped.remove(moduleGroup.identifier);
            }
        }

        public static @Nullable ModuleGroup getModuleGroup(@Nonnull ResourceLocation groupIdentifier) {
            Integer id = identifiersToId.get(groupIdentifier);
            if (id == null) {
                return null;
            } else {
                //return new ModuleGroup(groupIdentifier, groupDefinitions.get(id), featureCount.get(id)); // FIXME Unused
                return new ModuleGroup(groupIdentifier, contains.get(id), featureCount.get(id));
            }
        }

        /**
         * Internal method to add a ModuleSingle to the registry.
         * @param moduleSingle The ModuleSingle to add.
         * @since 3.0.0
         */
        protected static void tryAddingModule(MutableModuleSingle moduleSingle) {
            Integer nameExistingId = identifiersToId.get(moduleSingle.identifier);

            // Check if the identifier we're trying to add already exists.
            if (nameExistingId != null) {
                // The name we're trying to add already exists. This is strange and normally shouldn't be the case
                // Warn about double registration.
                Constants.LOG.warn("Trying to register already registered ModuleSingle [id = {}], aka {}",
                        nameExistingId, moduleSingle.identifier);
                featureCount.put(nameExistingId, featureCount.get(nameExistingId) + (moduleSingle.isFeature ? 1 : 0));
            } else {
                // The ModuleSingle is not yet specified. We can safely add it as a new ModuleSingle.
                Integer newId = moduleSingleId--;
                identifiersToId.put(moduleSingle.identifier, newId);
                knownModuleLikeIdentifiers.put(newId, new HashSet<>());
                knownModuleLikeIdentifiers.get(newId).add(moduleSingle.identifier);
                featureCount.put(newId, moduleSingle.isFeature ? 1 : 0);
                containedIn.put(newId, new HashSet<>());
            }

            // Check if there are holder mappings to fill and do so if appropriate
            if (holderNotMapped.contains(moduleSingle.identifier)) {
                holderMapping.put(identifiersToId.get(
                                new ResourceLocation("placeholder_" + moduleSingle.identifier.toString())),
                        identifiersToId.get(moduleSingle.identifier));
                holderNotMapped.remove(moduleSingle.identifier);
            }
        }

        public static @Nullable ModuleSingle getModuleSingle(@Nonnull ResourceLocation groupIdentifier) {
            Integer id = identifiersToId.get(groupIdentifier);
            if (id == null) {
                return null;
            } else {
                return new ModuleSingle(groupIdentifier, featureCount.get(id));
            }
        }

        /**
         * Internal method to add a ModuleHolder to the registry.
         * @param moduleHolder The ModuleHolder to add.
         * @since 3.0.0
         */
        protected static void tryAddingHolder(ModuleHolder moduleHolder) {
            Integer nameExistingId = identifiersToId.get(moduleHolder.getIdentifier());
            boolean canBeMapped = false;



            if (identifiersToId.get(moduleHolder.placeholderFor) != null) {
                canBeMapped = true;
            }


            // Check if the identifier we're trying to add already exists.
            if (nameExistingId != null) {
                // The name we're trying to add already exists. This is strange and normally shouldn't be the case
                // Warn about double registration.
                Constants.LOG.warn("Trying to register already registered ModuleHolder [id = {}], aka {}",
                        nameExistingId, moduleHolder.getIdentifier());
                featureCount.put(nameExistingId, featureCount.get(nameExistingId) + (moduleHolder.isFeature ? 1 : 0));
            } else {
                // The ModuleHolder is not yet specified. We can safely add it as a new ModuleHolder.
                Integer newId = moduleSingleId--;
                identifiersToId.put(moduleHolder.getIdentifier(), newId);
                knownModuleLikeIdentifiers.put(newId, new HashSet<>());
                knownModuleLikeIdentifiers.get(newId).add(moduleHolder.getIdentifier());
                featureCount.put(newId, moduleHolder.isFeature ? 1 : 0);
                containedIn.put(newId, new HashSet<>());
                if (!canBeMapped) {
                    holderMapping.put(newId, null);
                    holderNotMapped.add(moduleHolder.placeholderFor);
                } else {
                    holderMapping.put(newId, identifiersToId.get(moduleHolder.placeholderFor));
                }
            }
        }

        public static @Nullable ModuleHolder getModuleHolderByInternalId(@Nonnull ResourceLocation groupIdentifier) {
            Integer id = identifiersToId.get(groupIdentifier);
            if (id == null) {
                return null;
            } else {
                return new ModuleHolder(groupIdentifier, featureCount.get(id));
            }
        }
    }

}

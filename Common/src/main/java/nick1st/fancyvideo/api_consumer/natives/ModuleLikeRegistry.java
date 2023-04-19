package nick1st.fancyvideo.api_consumer.natives;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import nick1st.fancyvideo.Constants;

import java.util.*;
import java.util.stream.Collectors;

public final class ModuleLikeRegistry {

    // Common registries
    private static final Map<ResourceLocation, Identifier> locationToIdentifier = new HashMap<>();
    private static final Map<Identifier, Set<ResourceLocation>> identifierToLocations = new HashMap<>();



    // Set<ResourceLocation>: HoldersThatAreNotYetMapped // This is actually the identifier it maps to.
    private static final Set<ResourceLocation> holderNotMapped = new HashSet<>();

    // Integer: Id | Integer: MappedTo
    private static final Map<Identifier, Identifier> holderMapping = new HashMap<>();

    // Integer: Id | Set<Integer>: Id Contains Set
    private static final BiMap<Identifier, Set<Identifier>> contains = HashBiMap.create();

    // Integer: Id | Set<Integer>: ContainedInId
    private static final Map<Identifier, Set<Identifier>> containedIn = new HashMap<>();

    /**
     * Private Constructor to hide the implicit public one.
     * @since 3.0.0
     */
    private ModuleLikeRegistry() {

    }

    /**
     * TODO
     * @param moduleLike
     */
    static void addModuleLikeOfAnyKind(ModuleLike moduleLike) {
        // Start with checking the type
        if (moduleLike instanceof ModuleSingle moduleSingle) {
            tryAddingModule(moduleSingle);
        } else if (moduleLike instanceof MutableModuleGroup moduleGroup) {
            tryAddingGroup(moduleGroup);
        } else if (moduleLike instanceof  ModuleHolder moduleHolder) {
            tryAddingHolder(moduleHolder);
        }
    }

    /**
     * Internal method to add a ModuleSingle to the registry.
     * @param moduleSingle The ModuleSingle to add.
     * @since 3.0.0
     */
    private static void tryAddingModule(ModuleSingle moduleSingle) {
        Identifier locationExistingIdentifier = locationToIdentifier.get(moduleSingle.getIdentifier());

        // Check that the identifier we're trying to add does not already exist.
        if (locationExistingIdentifier == null) {
            // The ModuleSingle is not yet known. Register it.
            Identifier newId = new Identifier(Identifier.Type.MODULE);
            locationToIdentifier.put(moduleSingle.getIdentifier(), newId);
            identifierToLocations.put(newId, Set.of(moduleSingle.identifier));
            containedIn.put(newId, new HashSet<>());
        }
    }

    /**
     * Internal method to add a ModuleGroup to the registry.
     * @param moduleGroup The ModuleGroup to add.
     * @since 3.0.0
     */
    private static void tryAddingGroup(MutableModuleGroup moduleGroup) {
        Identifier locationExistingIdentifier = locationToIdentifier.get(moduleGroup.identifier);

        Set<Identifier> moduleGroupDeclaration = moduleGroup.containedModules.stream().map(moduleLike ->
                (moduleLike instanceof ModuleHolder moduleHolder) ? mappedOrHolderId(moduleHolder)
                        : locationToIdentifier.get(moduleLike.getIdentifier())).collect(Collectors.toUnmodifiableSet());

        Identifier groupDeclarationExistingIdentifier = contains.inverse().get(moduleGroupDeclaration);


        // Check that the identifier we're trying to add does not already exist.
        if (locationExistingIdentifier == null) {
            // It does not exist, meaning it is safe to register it
            Identifier identifier; // This identifier is either the newId or the id of an equal group.
            if (groupDeclarationExistingIdentifier == null) {
                // Neither the name nor the group declaration is known yet. We can safely add it as a new ModuleGroup.
                Identifier newId = new Identifier(Identifier.Type.GROUP);
                contains.put(newId, moduleGroupDeclaration);
                identifierToLocations.put(newId, new HashSet<>());
                containedIn.put(newId, new HashSet<>());
                identifier = newId;

                moduleGroupDeclaration.forEach(moduleIdentifier -> containedIn.get(moduleIdentifier).add(newId));
            } else {
                // The group is already registered at a different location. Add it as an alias
                identifier = groupDeclarationExistingIdentifier;
            }
            locationToIdentifier.put(moduleGroup.identifier, identifier);
            identifierToLocations.get(identifier).add(moduleGroup.identifier);
        } else {
            // It does exist, meaning either we
            // - got a group (same or another)
            // - got a holder

            // Check if the already existing identifier is a holder
            if (locationExistingIdentifier.isHolder()) {
                // It is a holder
                if (groupDeclarationExistingIdentifier != null) {
                    // We already got a matching group declaration. This means that the holder currently in place (and this group) is an alias of the matching declaration.
                    mapHolder(locationExistingIdentifier, groupDeclarationExistingIdentifier);
                } else {
                    // There is no matching group declaration. This Group resolves the holder.
                    Identifier newId = new Identifier(Identifier.Type.GROUP);
                    containedIn.put(newId, new HashSet<>());
                    identifierToLocations.put(newId, new HashSet<>());
                    // Add the contained modules and set their state to being contained in this
                    contains.put(newId, moduleGroupDeclaration);
                    moduleGroupDeclaration.forEach(moduleIdentifier -> containedIn.get(moduleIdentifier).add(newId));
                    // Map the holder
                    mapHolder(locationExistingIdentifier, newId);
                }
            } else {
                // It is not a holder, so it's a group
                if (groupDeclarationExistingIdentifier != null) {
                    // We got a group with a known location and a known declaration.
                    if (!locationExistingIdentifier.equals(groupDeclarationExistingIdentifier)) {
                        // However they aren't the same. This should not happen.
                        // The name is already known but as a different ModuleGroup. This is fatal.
                        Constants.LOG.error("Trying to add an alias ({}) for a ModuleGroup, however the known location is different from the known declaration:",
                                moduleGroup.identifier);
                        Constants.LOG.error("   - Location   :   id={}, declaration=[{}], locations=[{}]",
                                locationExistingIdentifier,
                                contains.get(locationExistingIdentifier).stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")),
                                identifierToLocations.get(locationExistingIdentifier).stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
                        Constants.LOG.error("   - Declaration:   id={}, declaration=[{}], locations=[{}]",
                                groupDeclarationExistingIdentifier,
                                contains.get(groupDeclarationExistingIdentifier).stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")),
                                identifierToLocations.get(groupDeclarationExistingIdentifier).stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
                        throw new RuntimeException("Trying to add an alias for a ModuleGroup, however the known location is different from the known declaration.");
                        // TODO Throw a better Exception
                    }
                    // Else we got an exact duplicate. Ignore this.
                } else {
                    // The location exists already, the declaration is unknown and the known location is not a holder, meaning we got a conflict.
                    Constants.LOG.error("Trying to add a ModuleGroup ({}), however the location is known as a different declaration already:",
                            moduleGroup.identifier);
                    Constants.LOG.error("   - Old:   id={}, declaration=[{}], locations=[{}]",
                            locationExistingIdentifier,
                            contains.get(locationExistingIdentifier).stream().sorted().map(Identifier::toString).collect(Collectors.joining(",")),
                            identifierToLocations.get(locationExistingIdentifier).stream().sorted().map(ResourceLocation::toString).collect(Collectors.joining(",")));
                    Constants.LOG.error("   - New:   id={}, declaration=[{}], locations=[{}]",
                            "unset",
                            moduleGroup.containedModules.stream().sorted().map(location -> locationToIdentifier.get(location).toString()).collect(Collectors.joining(",")),
                            moduleGroup.identifier);
                    throw new RuntimeException("Trying to add a ModuleGroup, however the location is known as a different declaration already.");
                    // TODO Throw a better Exception
                }
            }
        }
    }

    /**
     * Internal method to add a ModuleHolder to the registry.
     * @param moduleHolder The ModuleHolder to add.
     * @since 3.0.0
     */
    private static void tryAddingHolder(ModuleHolder moduleHolder) {
        Identifier locationExistingIdentifier = locationToIdentifier.get(moduleHolder.getIdentifier());

        // Check if the location of this holder is already known.
        if (locationExistingIdentifier != null) {
            // The location is already existing, so there's no need for us to add the holder.
        } else {
            // The location is not yet known. We add the holder as a placeholder.
            Identifier newId = new Identifier(Identifier.Type.HOLDER);

            containedIn.put(newId, new HashSet<>());
            holderNotMapped.add(moduleHolder.getIdentifier());
            identifierToLocations.put(newId, Set.of(moduleHolder.getIdentifier()));
            locationToIdentifier.put(moduleHolder.getIdentifier(), newId);
        }
    }

    /**
     * Assumes that the mapTo Identifier is already existing in {@link #containedIn} and {@link #identifierToLocations}.
     * @param holderIdentifier
     * @param mapTo
     */
    private static void mapHolder(Identifier holderIdentifier, Identifier mapTo) {
        // Update references in the containedIn map
        Set<Identifier> holderWasContainedIn = containedIn.get(holderIdentifier);

        // We need to check that mapping the contains BiMap does not create an identical value.
        // (This means we discovered that a group is identical to a group containing the holder)

        containedIn.get(mapTo).addAll(holderWasContainedIn);
        containedIn.remove(holderIdentifier);

        // No need to directly update contains, as a holder can't contain anything.
        // However, it can still be contained!
        holderWasContainedIn.forEach(groupIdentifierWhereTheHolderWasContained -> {
            ImmutableSet<Identifier> mappedGroupDeclaration = Sets.symmetricDifference(
                    contains.get(groupIdentifierWhereTheHolderWasContained),
                    Set.of(holderIdentifier, mapTo)
            ).immutableCopy();
            try {
                contains.put(groupIdentifierWhereTheHolderWasContained,
                        mappedGroupDeclaration); // FIXME Crash because this can create identical values in the BiMap
            } catch (IllegalArgumentException ignored) {
                // We discovered that a group is identical to a group containing the holder. Because of this, we
                // actually need to remap that group.
                mapHolder(groupIdentifierWhereTheHolderWasContained, contains.inverse().get(mappedGroupDeclaration));
                //Set<Identifier> groupsContainingTheRemovedGroup = contains.get(groupIdentifierWhereTheHolderWasContained);
                contains.remove(groupIdentifierWhereTheHolderWasContained);
                mappedGroupDeclaration.forEach(group ->
                        containedIn.get(group).remove(groupIdentifierWhereTheHolderWasContained));
            }
        });

        // Add the new mapping to the holder map
        holderMapping.put(holderIdentifier, mapTo); // TODO Check if we need this map in the future (the goal is we don't)

        // Update the identifierToLocations map, by adding the group.
        ResourceLocation holderLocation = identifierToLocations.get(holderIdentifier).stream().findFirst().get();
        identifierToLocations.remove(holderIdentifier);
        identifierToLocations.get(mapTo).add(holderLocation);

        // Remove the holder from the unmapped holder set
        holderNotMapped.remove(holderLocation); // TODO Check if we need this set in the future (the goal is we don't)

        // Update the locationToIdentifier map
        locationToIdentifier.put(holderLocation, mapTo);
    }

    private static Identifier mappedOrHolderId(ModuleHolder moduleHolder) { // TODO FIXME This needs to be reworked due to planned changes to the Holders Internals
        Identifier holderId = locationToIdentifier.get(moduleHolder.getIdentifier()); // FIXME This might not be needed at all anymore.
        Identifier mappedToId = holderMapping.get(holderId);
        return (mappedToId == null) ? holderId : mappedToId;
    }

    private static void rerunGroupEqualityChecks() {
        boolean registryWasChanged = true;

        while (registryWasChanged) {
            registryWasChanged = singeltonDuplicateGroups();
        }
    }

    private static boolean singeltonDuplicateGroups() {
        Identifier[] groupDefinitionsKeys = contains.keySet().toArray(new Identifier[0]);
        Set<Identifier>[] groupDefinitionsValues = contains.values().toArray(new Set[0]);
        // Integer: Duplicate | Integer: MapToThis
        Map<Identifier, Identifier> remap = new HashMap<>();
        for (int i = 0; i < groupDefinitionsKeys.length; i++) {
            Set<Identifier> currentSet = groupDefinitionsValues[i];
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
            Set<Identifier> containedInRemapThing = contains.get(duplicate);
            contains.remove(duplicate);
            holderMapping.replaceAll((key, value) -> Objects.equals(value, duplicate) ? mapToThis : value);
            locationToIdentifier.replaceAll((key, value) -> Objects.equals(value, duplicate) ? mapToThis : value);


            Set<Identifier> duplicateContainedIn = containedIn.get(duplicate);
            duplicateContainedIn.forEach(duplicateContainedInGroup -> {
                contains.get(duplicateContainedInGroup).remove(duplicate);
                contains.get(duplicateContainedInGroup).add(mapToThis);
            });
            containedIn.remove(duplicate);
            if (!containedInRemapThing.isEmpty()) {
                // We need to remove this id from the values from contained in
                containedInRemapThing.forEach(containedDuplicate -> containedIn.get(containedDuplicate).remove(duplicate));
            }

            Set<ResourceLocation> duplicateKnowIdentifiers = identifierToLocations.get(duplicate);
            identifierToLocations.remove(duplicate);
            identifierToLocations.get(mapToThis).addAll(duplicateKnowIdentifiers);
        });

        return !remap.isEmpty();
    }
}

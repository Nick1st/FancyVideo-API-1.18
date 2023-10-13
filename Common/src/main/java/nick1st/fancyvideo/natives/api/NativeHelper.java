package nick1st.fancyvideo.natives.api;

import nick1st.fancyvideo.Constants;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * This class contains all code required to <br>
 * 1. Get what modules are available. <br>
 * 2. Get what modules have been requested. <br>
 * 3. Provide the requested and available modules. <br>
 * 4. Report back what modules are available.
 *
 * @since 3.0.0
 */
public class NativeHelper {
    static ServiceLoader<AvailableNativeListProvider> available = ServiceLoader.load(AvailableNativeListProvider.class);
    static ServiceLoader<RequiredNativeListProvider> required = ServiceLoader.load(RequiredNativeListProvider.class);

    public static Iterator<ServiceLoader.Provider<AvailableNativeListProvider>> providers(boolean refresh) {
        if (refresh) {
            available.reload();
        }
        return available.stream().filter(provider -> provider.type().getName().endsWith("Forge")).iterator();
    }

    static List<String> installedModules = new ArrayList<>();

    public static Set<NativeListEntry> load() {
        Set<ServiceLoader.Provider<AvailableNativeListProvider>> availableModulesServices
                = available.stream().filter(list -> list.get().isListValid(Constants.DLL_VERSION)).collect(Collectors.toSet());

        Set<ServiceLoader.Provider<RequiredNativeListProvider>> requestedModulesServices
                = required.stream().collect(Collectors.toSet());

        Map<NativeListEntry, Set<Class<? extends NativeListProviders>>> availableModules = new HashMap<>();
        availableModulesServices.forEach(provider -> buildNativeListEntrySets(availableModules, provider.get()));
        sortNativeListEntrySets(availableModules);

        Map<NativeListEntry, Set<Class<? extends NativeListProviders>>> requiredModules = new HashMap<>();
        Map<NativeListEntry, Set<Class<? extends NativeListProviders>>> optionalModules = new HashMap<>();
        requestedModulesServices.forEach(provider -> {
            RequiredNativeListProvider p = provider.get();
            if (p.failGlobally()) {
                buildNativeListEntrySets(requiredModules, p);
            } else {
                buildNativeListEntrySets(optionalModules, p);
            }
        });

        Set<NativeListEntry> unavailable = new HashSet<>();

        requiredModules.forEach((nativeListEntry, classSet) -> {
            Iterator<Class<? extends NativeListProviders>> i = availableModules.get(nativeListEntry).iterator();
            boolean success = false;
            while (!success) {
                if (i.hasNext()) {
                    Class<? extends NativeListProviders> clazz = i.next();
                    try {
                        AvailableNativeListProvider currentAvailableList = ((AvailableNativeListProvider) clazz.getConstructors()[0].newInstance());
                        if (currentAvailableList.isSpecial()) {
                            System.out.println("WOW!");
                        }
                    } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                            success = ((AvailableNativeListProvider) clazz.getConstructors()[0].newInstance()).installModule(nativeListEntry); //TODO Replace Reflection with static method call
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                        }
                } else {
                    success = true;
                    Constants.NO_LIBRARY_MODE = true;
                }
            }
        });

        optionalModules.forEach((nativeListEntry, classSet) -> {
            Iterator<Class<? extends NativeListProviders>> i = availableModules.get(nativeListEntry).iterator();
            boolean success = false;
            while (!success) {
                if (i.hasNext()) {
                    try {
                        success = ((AvailableNativeListProvider) i.next().getConstructors()[0].newInstance()).installModule(nativeListEntry); //TODO Replace Reflection with static method call
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException ignored) {
                    }
                } else {
                    success = true;
                    unavailable.add(nativeListEntry);
                }
            }
        });

        return unavailable;
    }

    /**
     * Internal methode to build a quite complicated Map for comparison.
     * @since 3.0.0
     */
    public static void buildNativeListEntrySets(Map<NativeListEntry, Set<Class<? extends NativeListProviders>>> map, NativeListProviders provider) {
        if (provider.getModules() != null) {
            for (NativeListEntry module : provider.getModules()) {
                map.computeIfAbsent(module, k -> new HashSet<>());
                map.get(module).add(provider.getClass());
            }
        }
        /*if (provider.getModuleGroups() != null) {
            for (NativeGroup group: provider.getModuleGroups()) {
                for (NativeListEntry module: group.getModules()) {
                    map.computeIfAbsent(module, k -> new HashSet<>());
                    map.get(module).add(provider.getClass());
                }
            }
        }*/
    }

    /**
     * Internal methode to sort Service Providers by priority;
     * @since 3.0.0
     */
    public static void sortNativeListEntrySets(Map<NativeListEntry, Set<Class<? extends NativeListProviders>>> map) {
        Map.copyOf(map).forEach((nativeListEntry, classSet) -> map.put(nativeListEntry, classSet.stream().sorted((o1, o2) -> {
            try {
                return o1.getDeclaredField("priority").getShort(null) - o2.getDeclaredField("priority").getShort(null);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toCollection(LinkedHashSet::new))));
    }

    public static void initNativeRegistry() throws Exception {
        Iterator<AvailableNativeListProvider> available = ServiceLoader.load(AvailableNativeListProvider.class).stream()
                .filter(provider -> provider.type().getName().endsWith("Forge"))
                .filter(list -> list.get().isListValid(Constants.DLL_VERSION))
                .map(ServiceLoader.Provider::get).iterator();
        Iterator<RequiredNativeListProvider> required = ServiceLoader.load(RequiredNativeListProvider.class).iterator();

        while (available.hasNext()) {
            AvailableNativeListProvider next = available.next();
            if (next.isListValid(Constants.DLL_VERSION)) {
                Arrays.stream(next.getModules()).forEach(module -> {
                    if (!ModuleRegistry.registry.containsKey(module.getIdentifier())) {
                        ModuleRegistry.registry.put(module.getIdentifier(), new ModuleRegistryEntry(module.getIdentifier(), new LinkedHashSet<>(), new LinkedHashSet<>()));
                    }
                    ModuleRegistry.registry.get(module.getIdentifier()).addAvailable(next, next.installType());
                });
            }
        }

        while (required.hasNext()) {
            RequiredNativeListProvider next = required.next();
            Arrays.stream(next.getModules()).forEach(module -> {
                if (!ModuleRegistry.registry.containsKey(module.getIdentifier())) {
                    ModuleRegistry.registry.put(module.getIdentifier(), new ModuleRegistryEntry(module.getIdentifier(), new LinkedHashSet<>(), new LinkedHashSet<>()));
                }
                ModuleRegistry.registry.get(module.getIdentifier()).addRequester(next, next.requirementType());
            });
        }

        // Install modules
        NativeHelper.install();
    }

    /**
     * Internal method to check requirements
     * @since 3.0.0
     */
    public static void install() {
        // Start by checking if there are module requirements that can only be satisfied by special installs
        Map<String, ModuleRegistryEntry> bothInstalls = new HashMap<>();
        Map<String, ModuleRegistryEntry> moduleInstall = new HashMap<>();
        Map<String, ModuleRegistryEntry> specialInstall = new HashMap<>();
        Map<String, ModuleRegistryEntry> noInstall = new HashMap<>();
        Map<String, ModuleRegistryEntry> notRequested = new HashMap<>();
        ModuleRegistry.registry.forEach((identifier, entry) -> {
            if (entry.isRequested()) {
                if (entry.hasModuleInstall()) {
                    if (entry.hasSpecialInstall()) {
                        // This module has at least a special and a module install, so for those we'd actually prefer the modular ones
                        bothInstalls.put(identifier, entry);
                    } else {
                        // This module has only a module install, so it's not too complicated to satisfy the request
                        moduleInstall.put(identifier, entry);
                    }
                } else {
                    if (entry.hasSpecialInstall()) {
                        // This module has only a special install, now that's complicated
                        specialInstall.put(identifier, entry);
                    } else {
                        // This module has no install, sorry to the requesters
                        noInstall.put(identifier, entry);
                    }
                }
            } else {
                // This module was not requested, so don't bother with it
                notRequested.put(identifier, entry);
            }
        });

        // Now we need to decide what modules to load. We use a vote to do that. Each RequiredNativeListProvider does
        // add +1 to the vote for a module.
        // Start with special install only modules, as those are the most difficult. The request to load the whole
        // installation method is equal to each module provided * the votes for this module. If all requests can be
        // satisfied by module installs, then that method is preferred. If more than one special installation can
        // satisfy a request, the one installing LESS modules is preferred. If none of the previous rules is matched, we
        // decide based on the priority.
        Map<AvailableNativeListProvider, Integer> specialInstallRequestScore = new HashMap<>(); // Number of total requests that can be satisfied by a provider
        Map<AvailableNativeListProvider, Integer> specialInstallUnusedScore = new HashMap<>(); // Count of modules provided by install that have not been requested.
        Map<AvailableNativeListProvider, Integer> specialInstallBlockingScore = new HashMap<>(); // Blocking means this vote count can no
        Map<AvailableNativeListProvider, Set<String>> conflicting = new HashMap<>(); // Holds the modules that cause the conflict between this and all other providers
        // longer be satisfied if we choose this module

        // A set of all providers that provide at least one special-installation only module
        Set<AvailableNativeListProvider> allSpecialInstallProviders = new HashSet<>();
        specialInstall.forEach((identifier, entry) -> allSpecialInstallProviders.addAll(entry.getSpecialProviders()));


        allSpecialInstallProviders.forEach(specialProvider -> {
            // Get the score this installation provider can satisfy
            int requestCount = Arrays.stream(specialProvider.getModules()).mapToInt(modules ->
                    ModuleRegistry.registry.get(modules.getIdentifier()).getTotalRequests()).sum();
            specialInstallRequestScore.put(specialProvider, requestCount);

            // Get the count of unused modules this provider would install
            int unusedCount = (int) Arrays.stream(specialProvider.getModules())
                    .filter(modules -> !ModuleRegistry.registry.get(modules.getIdentifier()).isRequested()).count();
            specialInstallUnusedScore.put(specialProvider, unusedCount);

            // Get the score that we can no longer satisfy if we choose this module
            Iterator<AvailableNativeListProvider> otherSpecialProviderIterator = allSpecialInstallProviders.iterator();
            Set<String> unsatisfied = new HashSet<>();
            // Loop over all special only installation providers
            while (otherSpecialProviderIterator.hasNext()) {
                AvailableNativeListProvider otherSpecialProvider = otherSpecialProviderIterator.next();
                if (otherSpecialProvider == specialProvider) {
                    // We got the provider we're currently calculating for. Skipping.
                } else {
                    // Check if there is an intersection between this special provider and the other one.
                    List<NativeListEntry> modulesIntersecting = Arrays.asList(otherSpecialProvider.getModules());
                    modulesIntersecting.retainAll(Arrays.asList(specialProvider.getModules()));
                    if (!modulesIntersecting.isEmpty()) {
                        // There is an intersection. Now count how many modules may not be able to be satisfied.
                        // This does not include modules that:
                        // - have modular installation
                        // - have not been requested
                        // TODO Note that this is flawed, as it is still possible a module can be provided by a
                        //  non-conflicting special install

                        // Remove all modules from the intersection list that have not been requested.
                        List<String> modulesIntersectingRepresentation = new ArrayList<>(modulesIntersecting.stream().map(NativeListEntry::getIdentifier).toList());
                        modulesIntersectingRepresentation.removeAll(notRequested.keySet());

                        // Filter the remaining conflicts for modular installation
                        unsatisfied.addAll(modulesIntersectingRepresentation.stream()
                                .filter(identifier -> !ModuleRegistry.registry.get(identifier).hasModuleInstall()).toList());
                    }
                }
            }
            // Now sum how often the unsatisfied modules were requested
            int unsatisfiedCount = unsatisfied.stream().mapToInt(identifier -> ModuleRegistry.registry.get(identifier).getTotalRequests()).sum();
            specialInstallBlockingScore.put(specialProvider, unsatisfiedCount);
        });



        // Now we got all stats we need to choose if / what special provider we use. //TODO flawed code
        List<AvailableNativeListProvider> providersWeMightUse = new ArrayList<>();
        List<AvailableNativeListProvider> providersWeWontUse = new ArrayList<>();
        specialInstallRequestScore.forEach((provider, score) -> {
            if (specialInstallBlockingScore.get(provider) > score) {
                // This special installation blocks more modules than it provides. It is not useful for us.
                providersWeWontUse.add(provider);
            } else {
                // This special installation provides more modules than it blocks. It is a candidate for use.
                providersWeMightUse.add(provider);
            }
        });

        // Now try to get the final provider List // TODO Using a flawed way, as this won't get us the best possible environment.
        providersWeMightUse.sort(Comparator.comparingInt(specialInstallRequestScore::get));
        List<AvailableNativeListProvider> providersWeWillUse = new ArrayList<>();
        List<NativeListEntry> modulesAlreadyPresent = new ArrayList<>();
        Iterator<AvailableNativeListProvider> iterator = providersWeMightUse.iterator();
        while (iterator.hasNext()) {
            AvailableNativeListProvider provider = iterator.next();
            // Check if a module this special provider provides has already been supplied.
            List<NativeListEntry> providerModules = Arrays.asList(provider.getModules());
            providerModules.retainAll(modulesAlreadyPresent);
            if (providerModules.isEmpty()) {
                // This has no clash yet, so add it
                providersWeWillUse.add(provider);
                // And add all it's modules to the conflict list
                modulesAlreadyPresent.addAll(Arrays.asList(provider.getModules()));
            }
        }

        installSpecialProviders(providersWeWillUse, modulesAlreadyPresent);
        installModuleProviders(bothInstalls, moduleInstall);
    }

    /**
     * Internal method to install module installation module providers
     * @param bothInstalls
     * @param moduleInstall
     * @since 3.0.0
     */
    private static void installModuleProviders(Map<String, ModuleRegistryEntry> bothInstalls, Map<String, ModuleRegistryEntry> moduleInstall) {
        // Install all requested module only installation providers
        moduleInstall.forEach((identifier, moduleRegistryEntry) -> {
            moduleRegistryEntry.providers().iterator().next().installModule(new NativeListEntry(identifier)); // TODO Check if this wields the right result
            installedModules.add(identifier);
        });
        // Install modules that have not been installed by special installation procedure
        bothInstalls.forEach((identifier, moduleRegistryEntry) -> {
            if (!installedModules.contains(identifier)) {
                moduleRegistryEntry.providers().iterator().next().installModule(new NativeListEntry(identifier)); // TODO Check if this wields the right result
            }
        });
    }

    /**
     * Internal method to install special installation module providers
     * @param providersWeWillUse
     * @param modulesAlreadyPresent
     * @since 3.0.0
     */
    private static void installSpecialProviders(List<AvailableNativeListProvider> providersWeWillUse, List<NativeListEntry> modulesAlreadyPresent) {
        Iterator<AvailableNativeListProvider> iterator = providersWeWillUse.iterator();
        while (iterator.hasNext()) {
            AvailableNativeListProvider provider = iterator.next();
            // Invoke installation using reflection
            try {
                boolean success = (boolean) provider.getClass().getDeclaredMethod("install").invoke(null);
                if (success) {
                    installedModules.addAll(Arrays.stream(provider.getModules()).map(NativeListEntry::getIdentifier).toList());
                } else {
                    Constants.LOG.warn("A special provider failed with installing it's modules- Provider: " + provider.getClass().getSimpleName());
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Internal methode to sort Service Providers by priority.
     *
     * @return
     * @since 3.0.0
     */
    public static Set<AvailableNativeListProvider> sortNativeProviders(Set<AvailableNativeListProvider> set) {
        return set.stream().sorted(Comparator.comparingInt(NativeListProviders::priority).reversed()).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

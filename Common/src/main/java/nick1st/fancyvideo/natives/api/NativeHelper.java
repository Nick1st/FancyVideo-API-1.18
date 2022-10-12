package nick1st.fancyvideo.natives.api;

import nick1st.fancyvideo.Constants;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                    try {
                        success = ((AvailableNativeListProvider) i.next().getConstructors()[0].newInstance()).installModule(nativeListEntry); //TODO Replace Reflection with static method call
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
        if (provider.getModuleGroups() != null) {
            for (NativeGroup group: provider.getModuleGroups()) {
                for (NativeListEntry module: group.getModules()) {
                    map.computeIfAbsent(module, k -> new HashSet<>());
                    map.get(module).add(provider.getClass());
                }
            }
        }
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
}

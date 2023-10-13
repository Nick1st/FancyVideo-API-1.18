package nick1st.fancyvideo.natives.api;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class ModuleRegistryEntry {
    private final Set<AvailableNativeListProvider> providers;

    private final Set<AvailableNativeListProvider> moduleProviders;
    private final Set<AvailableNativeListProvider> specialProviders;


    private final Set<RequiredNativeListProvider> requesters;

    private final Set<RequiredNativeListProvider> optionalRequesters;
    private final Set<RequiredNativeListProvider> requiredRequesters;

    private final String identifier;
    private int hasSpecial = 0;

    private RequireState state = RequireState.NONE;



    private boolean isSatisfied = false;

    public ModuleRegistryEntry(String identifier, Set<AvailableNativeListProvider> providers, Set<RequiredNativeListProvider> requesters) {
        this.providers = providers;
        this.requesters = requesters;
        this.identifier = identifier;

        moduleProviders = new TreeSet<>();
        specialProviders = new TreeSet<>();

        optionalRequesters = new HashSet<>();
        requiredRequesters = new HashSet<>();
    }

    public Set<AvailableNativeListProvider> providers() {
        return providers;
    }

    public Set<RequiredNativeListProvider> requesters() {
        return requesters;
    }

    public int containsSpecial() {
        return hasSpecial;
    }

    public void addSpecial() {
        hasSpecial += 1;
    }

    public void addRequester(RequiredNativeListProvider listProvider, RequireState state) {
        if (state == RequireState.REQUIRED) {
            requiredRequesters.add(listProvider);
        } else {
            optionalRequesters.add(listProvider);
        }
        requesters.add(listProvider); // TODO Legacy or main?
    }

    public void updateState(RequireState state) {
        if (this.state == RequireState.NONE) {
            this.state = state;
        } else if (state == RequireState.OPTIONAL) {
            this.state = RequireState.OPTIONAL;
        } else if (state == RequireState.REQUIRED) {
            this.state = RequireState.REQUIRED;
        }
    }

    public void addAvailable(AvailableNativeListProvider listProvider, InstallType type) {
        if (type == InstallType.MODULES) {
            moduleProviders.add(listProvider);
        } else {
            specialProviders.add(listProvider);
        }
        providers.add(listProvider); // TODO Legacy or main?
    }

    public boolean isRequested() {
        return !optionalRequesters.isEmpty() || !requiredRequesters.isEmpty();
    }

    public boolean hasSpecialInstall() {
        return !moduleProviders.isEmpty();
    }

    public boolean hasModuleInstall() {
        return !specialProviders.isEmpty();
    }

    public Set<AvailableNativeListProvider> getSpecialProviders() {
        return specialProviders;
    }

    public int getTotalRequests() {
        return optionalRequesters.size() + requiredRequesters.size();
    }

    protected void setSatisfied() {
        isSatisfied = true;
    }
}

package nick1st.fancyvideo.api_consumer.provider;

import net.minecraft.resources.ResourceLocation;

public abstract class Provider {

    public abstract ResourceLocation providerName();

    public abstract ResourceLocation[] providedModules();

    public abstract boolean isValid();

    public boolean isGroupProvider() {
        return false;
    }

    @Override
    public int hashCode() {
        return providerName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Provider p) {
            return providerName().equals(p.providerName());
        }
        return false;
    }
}

package dev.erikcodes.ae2nobyproduct.fabric;

import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import dev.erikcodes.ae2nobyproduct.fabric.config.FabricConfig;
import dev.erikcodes.ae2nobyproduct.fabric.persistence.ByproductToggleAccess;
import net.minecraft.world.entity.player.Player;

/**
 * Fabric implementation of the {@link ByproductConfig.Provider} seam: settings come from the JSON
 * {@link FabricConfig}, and the per-player toggle is stored on the {@code Player} by the persistence
 * mixins via {@link ByproductToggleAccess}. This is the Fabric counterpart to {@code ForgeByproductConfig},
 * so the shared handlers behave identically on both loaders.
 */
public final class FabricByproductConfig implements ByproductConfig.Provider {
    @Override public boolean enableFeature() { return FabricConfig.get().enableFeature; }
    @Override public boolean allowPlayerToggle() { return FabricConfig.get().allowPlayerToggle; }
    @Override public boolean defaultStrip() { return FabricConfig.get().defaultStrip; }
    @Override public boolean consumeOnUse() { return FabricConfig.get().consumeOnUse; }
    @Override public boolean showMessages() { return FabricConfig.get().showMessages; }

    @Override public boolean savedState(Player player, boolean def) {
        ByproductToggleAccess access = (ByproductToggleAccess) player;
        return access.ae2nobyproduct$hasToggle() ? access.ae2nobyproduct$getToggle() : def;
    }

    @Override public void setSavedState(Player player, boolean value) {
        ((ByproductToggleAccess) player).ae2nobyproduct$setToggle(value);
    }
}

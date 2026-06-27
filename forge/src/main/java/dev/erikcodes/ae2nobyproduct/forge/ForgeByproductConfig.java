package dev.erikcodes.ae2nobyproduct.forge;

import dev.erikcodes.ae2nobyproduct.config.Config;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import dev.erikcodes.ae2nobyproduct.core.ByproductState;
import net.minecraft.world.entity.player.Player;

/**
 * Forge implementation of {@link ByproductConfig.Provider}: settings come from the
 * {@code ForgeConfigSpec} {@link Config}, and the per-player toggle is persisted via
 * {@link ByproductState} (Forge's {@code Player.getPersistentData()}).
 */
public final class ForgeByproductConfig implements ByproductConfig.Provider {
    @Override public boolean enableFeature() { return Config.enableFeature(); }
    @Override public boolean allowPlayerToggle() { return Config.allowPlayerToggle(); }
    @Override public boolean defaultStrip() { return Config.defaultStrip(); }
    @Override public boolean savedState(Player player, boolean def) { return ByproductState.get(player, def); }
    @Override public void setSavedState(Player player, boolean value) { ByproductState.set(player, value); }
}

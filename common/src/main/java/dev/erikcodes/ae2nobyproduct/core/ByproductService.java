package dev.erikcodes.ae2nobyproduct.core;

import net.minecraft.world.entity.player.Player;

/**
 * Loader-agnostic decision logic for whether byproducts should be stripped for a given player.
 * Settings (and per-player toggle persistence) come from the platform via {@link ByproductConfig}.
 */
public final class ByproductService {
    private ByproductService() {}

    /** Effective on/off for this player given the installed config + saved state. */
    public static boolean effectiveFor(Player player) {
        ByproductConfig.Provider cfg = ByproductConfig.get();
        // Only touch per-player persistence when toggling is actually allowed. This keeps the
        // Forge-only getPersistentData() path off loaders that have no persistence.
        boolean saved = cfg.allowPlayerToggle()
                ? cfg.savedState(player, cfg.defaultStrip())
                : cfg.defaultStrip();
        return EffectiveState.compute(cfg.enableFeature(), cfg.allowPlayerToggle(), cfg.defaultStrip(), saved);
    }

    /** Whether to strip byproducts when this player encodes now. */
    public static boolean shouldStrip(Player player) {
        return effectiveFor(player);
    }
}

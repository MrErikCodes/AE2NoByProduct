package dev.erikcodes.ae2nobyproduct.core;

import dev.erikcodes.ae2nobyproduct.config.Config;
import net.minecraft.world.entity.player.Player;

public final class ByproductService {
    private ByproductService() {}

    /** Effective on/off for this player given config + saved state. */
    public static boolean effectiveFor(Player player) {
        boolean saved = ByproductState.get(player, Config.defaultStrip());
        return EffectiveState.compute(Config.enableFeature(), Config.allowPlayerToggle(), Config.defaultStrip(), saved);
    }

    /** Whether to strip byproducts when this player encodes now. */
    public static boolean shouldStrip(Player player) {
        return effectiveFor(player);
    }
}

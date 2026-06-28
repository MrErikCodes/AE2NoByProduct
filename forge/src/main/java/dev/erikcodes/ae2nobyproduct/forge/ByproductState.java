package dev.erikcodes.ae2nobyproduct.forge;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

/**
 * Forge per-player toggle persistence, backed by Forge's {@code Player.getPersistentData()} +
 * PERSISTED_NBT_TAG (which Forge copies across death/respawn for free). This is a Forge-only leaf,
 * so it lives in the {@code forge} package, NOT in the shared {@code core} package: Forge's
 * ModLauncher enforces JPMS modules and rejects a package that exists in both the platform module
 * and the bundled common module (split package). The Fabric equivalent is the persistence mixins.
 */
public final class ByproductState {
    private static final String SUBTAG = Player.PERSISTED_NBT_TAG; // "PlayerPersisted"
    private static final String KEY = "ae2nobyproduct:strip";
    private ByproductState() {}

    public static boolean get(Player player, boolean def) {
        CompoundTag root = player.getPersistentData();
        if (!root.contains(SUBTAG)) return def;
        CompoundTag sub = root.getCompound(SUBTAG);
        return sub.contains(KEY) ? sub.getBoolean(KEY) : def;
    }

    public static void set(Player player, boolean value) {
        CompoundTag root = player.getPersistentData();
        CompoundTag sub = root.getCompound(SUBTAG); // empty tag if absent
        sub.putBoolean(KEY, value);
        root.put(SUBTAG, sub);
    }
}

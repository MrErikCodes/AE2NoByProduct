package dev.erikcodes.ae2nobyproduct.core;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

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

package dev.erikcodes.ae2nobyproduct.core;

import dev.erikcodes.ae2nobyproduct.CommonMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Loader-agnostic per-player toggle persistence, stored as vanilla {@link SavedData} on the server's
 * overworld and keyed by player UUID. Being UUID-keyed (not entity-tied), it survives relog, server
 * restart, and death/respawn automatically, with no loader-specific code: this is pure vanilla
 * Minecraft, identical on Forge, Fabric, and NeoForge. The toggle only exists server-side, which is
 * where every read and write happens.
 */
public final class ByproductStore extends SavedData {
    private static final String NAME = CommonMod.MOD_ID + "_toggles";
    private static final String KEY = "toggles";

    private final Map<UUID, Boolean> toggles = new HashMap<>();

    public ByproductStore() {}

    //? if >=1.21 {
    /*public static ByproductStore load(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    *///?} else {
    public static ByproductStore load(CompoundTag tag) {
    //?}
        ByproductStore store = new ByproductStore();
        CompoundTag map = tag.getCompound(KEY);
        for (String id : map.getAllKeys()) {
            store.toggles.put(UUID.fromString(id), map.getBoolean(id));
        }
        return store;
    }

    @Override
    //? if >=1.21 {
    /*public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    *///?} else {
    public CompoundTag save(CompoundTag tag) {
    //?}
        CompoundTag map = new CompoundTag();
        toggles.forEach((id, value) -> map.putBoolean(id.toString(), value));
        tag.put(KEY, map);
        return tag;
    }

    private static ByproductStore of(MinecraftServer server) {
        //? if >=1.21 {
        /*return server.overworld().getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(ByproductStore::new, ByproductStore::load, null), NAME);
        *///?} else {
        return server.overworld().getDataStorage().computeIfAbsent(ByproductStore::load, ByproductStore::new, NAME);
        //?}
    }

    /** Read this player's saved toggle, or {@code def} if it is unset or the player is not on a server. */
    public static boolean getToggle(Player player, boolean def) {
        if (player instanceof ServerPlayer sp && sp.getServer() != null) {
            return of(sp.getServer()).toggles.getOrDefault(player.getUUID(), def);
        }
        return def;
    }

    /** Persist this player's toggle. No-op off the server. */
    public static void setToggle(Player player, boolean value) {
        if (player instanceof ServerPlayer sp && sp.getServer() != null) {
            ByproductStore store = of(sp.getServer());
            store.toggles.put(player.getUUID(), value);
            store.setDirty();
        }
    }
}

package dev.erikcodes.ae2nobyproduct.network;

import dev.architectury.networking.NetworkManager;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.client.ClientByproductState;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import dev.erikcodes.ae2nobyproduct.core.ByproductService;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * Loader-agnostic networking via Architectury {@link NetworkManager}, replacing the Forge-only
 * {@code SimpleChannel}. Two packets:
 * <ul>
 *   <li>{@link #SET_TOGGLE} (C2S): a player asks the server to flip their per-player toggle.</li>
 *   <li>{@link #SYNC_STATE} (S2C): the server pushes the effective state + config gates to a client
 *       so the toolbar button can render the right icon and visibility.</li>
 * </ul>
 *
 * <p>The settings and per-player persistence are reached through {@link ByproductConfig} (the shared
 * platform seam), so the handlers themselves are fully loader-agnostic.
 */
public final class ModNetworking {
    public static final ResourceLocation SET_TOGGLE = new ResourceLocation(CommonMod.MOD_ID, "set_toggle");
    public static final ResourceLocation SYNC_STATE = new ResourceLocation(CommonMod.MOD_ID, "sync_state");

    private ModNetworking() {}

    /**
     * Registers the C2S receiver. Safe to call on both physical sides (it only fires server-side, when
     * the server receives the packet). The S2C receiver is registered separately, client-only, via
     * {@link #initClient()}.
     */
    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SET_TOGGLE, (buf, ctx) -> {
            boolean enabled = buf.readBoolean();
            ctx.queue(() -> {
                if (!(ctx.getPlayer() instanceof ServerPlayer player)) return;
                ByproductConfig.Provider cfg = ByproductConfig.get();
                // Honour the server config: only apply the toggle when the feature is on and toggling is
                // allowed. Either way, echo the authoritative state back so the client's optimistic button
                // update is corrected when the server rejects (or otherwise changes) it.
                if (cfg.enableFeature() && cfg.allowPlayerToggle()) {
                    cfg.setSavedState(player, enabled);
                }
                sendSyncState(player);
            });
        });
    }

    /**
     * Registers the S2C receiver. MUST run on the physical client only: on Fabric this maps to
     * {@code ClientPlayNetworking}, which is absent on a dedicated server. Call it from
     * {@code EnvExecutor.runInEnv(Env.CLIENT, ...)}.
     */
    public static void initClient() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_STATE, (buf, ctx) -> {
            boolean effectiveState = buf.readBoolean();
            boolean featureEnabled = buf.readBoolean();
            boolean allowToggle = buf.readBoolean();
            ctx.queue(() -> ClientByproductState.update(effectiveState, featureEnabled, allowToggle));
        });
    }

    /** C2S: ask the server to set this player's toggle. */
    public static void sendSetToggle(boolean enabled) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(enabled);
        NetworkManager.sendToServer(SET_TOGGLE, buf);
    }

    /** S2C: push the effective state + config gates to one player. */
    public static void sendSyncState(ServerPlayer player) {
        ByproductConfig.Provider cfg = ByproductConfig.get();
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeBoolean(ByproductService.effectiveFor(player));
        buf.writeBoolean(cfg.enableFeature());
        buf.writeBoolean(cfg.allowPlayerToggle());
        NetworkManager.sendToPlayer(player, SYNC_STATE, buf);
    }
}

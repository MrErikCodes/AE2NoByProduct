package dev.erikcodes.ae2nobyproduct.network;

import dev.erikcodes.ae2nobyproduct.config.Config;
import dev.erikcodes.ae2nobyproduct.core.ByproductState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record C2SSetByproductRemoval(boolean enabled) {
    public static void encode(C2SSetByproductRemoval m, FriendlyByteBuf buf) { buf.writeBoolean(m.enabled); }
    public static C2SSetByproductRemoval decode(FriendlyByteBuf buf) { return new C2SSetByproductRemoval(buf.readBoolean()); }
    public static void handle(C2SSetByproductRemoval m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer p = ctx.get().getSender();
            if (p == null) return;
            if (!Config.enableFeature() || !Config.allowPlayerToggle()) return; // not permitted
            ByproductState.set(p, m.enabled);
        });
        ctx.get().setPacketHandled(true);
    }
}

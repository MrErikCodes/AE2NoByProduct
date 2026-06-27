package dev.erikcodes.ae2nobyproduct.network;

import dev.erikcodes.ae2nobyproduct.client.ClientByproductState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public record S2CSyncState(boolean effectiveState, boolean featureEnabled, boolean allowToggle) {
    public static void encode(S2CSyncState m, FriendlyByteBuf buf) {
        buf.writeBoolean(m.effectiveState); buf.writeBoolean(m.featureEnabled); buf.writeBoolean(m.allowToggle);
    }
    public static S2CSyncState decode(FriendlyByteBuf buf) {
        return new S2CSyncState(buf.readBoolean(), buf.readBoolean(), buf.readBoolean());
    }
    public static void handle(S2CSyncState m, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                ClientByproductState.update(m.effectiveState, m.featureEnabled, m.allowToggle)));
        ctx.get().setPacketHandled(true);
    }
}

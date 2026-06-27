package dev.erikcodes.ae2nobyproduct.network;

import dev.erikcodes.ae2nobyproduct.CommonMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class Network {
    private static final String PROTOCOL = "1";
    public static SimpleChannel CHANNEL;
    private Network() {}
    public static void register() {
        CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CommonMod.MOD_ID, "main"),
            () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
        int id = 0;
        CHANNEL.registerMessage(id++, C2SSetByproductRemoval.class,
            C2SSetByproductRemoval::encode, C2SSetByproductRemoval::decode, C2SSetByproductRemoval::handle);
        CHANNEL.registerMessage(id++, S2CSyncState.class,
            S2CSyncState::encode, S2CSyncState::decode, S2CSyncState::handle);
    }
}

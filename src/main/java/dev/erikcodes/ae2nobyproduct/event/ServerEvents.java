package dev.erikcodes.ae2nobyproduct.event;

import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.erikcodes.ae2nobyproduct.AE2NoByProduct;
import dev.erikcodes.ae2nobyproduct.config.Config;
import dev.erikcodes.ae2nobyproduct.core.ByproductService;
import dev.erikcodes.ae2nobyproduct.network.Network;
import dev.erikcodes.ae2nobyproduct.network.S2CSyncState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = AE2NoByProduct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ServerEvents {
    private ServerEvents() {}
    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open e) {
        if (e.getEntity() instanceof ServerPlayer sp && e.getContainer() instanceof PatternEncodingTermMenu) {
            Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> sp),
                new S2CSyncState(ByproductService.effectiveFor(sp), Config.enableFeature(), Config.allowPlayerToggle()));
        }
    }
}

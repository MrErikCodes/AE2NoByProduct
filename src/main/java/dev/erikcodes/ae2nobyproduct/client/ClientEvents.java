package dev.erikcodes.ae2nobyproduct.client;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import dev.erikcodes.ae2nobyproduct.AE2NoByProduct;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AE2NoByProduct.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {
    private ClientEvents() {}
    // Offsets relative to the GUI top-left; tuned in-game with runClient.
    private static final int OFFSET_X = 178;
    private static final int OFFSET_Y = 90;
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post e) {
        if (e.getScreen() instanceof PatternEncodingTermScreen<?> screen && ClientByproductState.showButton()) {
            AbstractContainerScreen<?> acs = screen;
            int x = acs.getGuiLeft() + OFFSET_X;
            int y = acs.getGuiTop() + OFFSET_Y;
            e.addListener(new ByproductToggleButton(x, y));
        }
    }
}

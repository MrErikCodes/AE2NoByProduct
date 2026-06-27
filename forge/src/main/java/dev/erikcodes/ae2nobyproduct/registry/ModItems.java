package dev.erikcodes.ae2nobyproduct.registry;

import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.item.ByproductRemoverItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = CommonMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModItems {

    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, CommonMod.MOD_ID);

    public static final RegistryObject<Item> BYPRODUCT_REMOVER =
        ITEMS.register("byproduct_remover",
            () -> new ByproductRemoverItem(new Item.Properties().stacksTo(1)));

    /** Wire the item registry onto the mod event bus. Call from the @Mod constructor. */
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

    @SubscribeEvent
    public static void addToCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(BYPRODUCT_REMOVER);
        }
    }
}

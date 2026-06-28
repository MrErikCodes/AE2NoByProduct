package dev.erikcodes.ae2nobyproduct.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.item.ByproductRemoverItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;

/**
 * Loader-agnostic item registration via Architectury. Replaces the Forge {@code DeferredRegister} +
 * {@code BuildCreativeModeTabContentsEvent} so the Byproduct Remover is registered and shown in the
 * creative menu identically on Forge and Fabric. Wired from {@link CommonMod#init()}.
 */
public final class ModItems {

    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(CommonMod.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> BYPRODUCT_REMOVER =
        ITEMS.register("byproduct_remover",
            () -> new ByproductRemoverItem(new Item.Properties().stacksTo(1)));

    /** Register the items and add the Byproduct Remover to the Tools &amp; Utilities creative tab. */
    public static void init() {
        ITEMS.register();
        CreativeTabRegistry.append(CreativeModeTabs.TOOLS_AND_UTILITIES, BYPRODUCT_REMOVER);
    }
}

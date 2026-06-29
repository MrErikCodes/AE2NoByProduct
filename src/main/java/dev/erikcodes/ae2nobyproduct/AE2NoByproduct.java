package dev.erikcodes.ae2nobyproduct;

/*? if fabric {*/
import net.fabricmc.api.ModInitializer;
/*?}*/

/*? if forge {*/
/*import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
*/
/*?}*/

/*? if neoforge {*/
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

*//*?}*/

/**
 * Mod entry point and the only loader-specific class in the mod. Each loader's bootstrap installs the
 * (Forge/NeoForge) mod event bus that Architectury needs for deferred registration, then hands off to
 * the shared {@link CommonMod#init()}. Fabric has no mod bus, so it simply delegates.
 */
/*? if forgeLike {*/
/*@Mod(CommonMod.MOD_ID)
public final class AE2NoByproduct {
*//*?}*/
/*? if fabric {*/
public final class AE2NoByproduct implements ModInitializer {
/*?}*/

    /*? if fabric {*/
    @Override
    public void onInitialize() {
        CommonMod.init();
    }
    /*?}*/

    /*? if forge {*/
    /*public AE2NoByproduct() {
        EventBuses.registerModEventBus(CommonMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CommonMod.init();
    }
    */
    /*?}*/

    /*? if neoforge {*/
    /*// NeoForge: Architectury auto-discovers the mod event bus (no registerModEventBus needed, unlike
    // Forge). The constructor params are required by NeoForge's @Mod entry shape but unused here.
    public AE2NoByproduct(IEventBus modEventBus, ModContainer modContainer) {
        CommonMod.init();
    }
    *//*?}*/
}

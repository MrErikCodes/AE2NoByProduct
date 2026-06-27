package dev.erikcodes.ae2nobyproduct.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.config.Config;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import dev.erikcodes.ae2nobyproduct.network.Network;
import dev.erikcodes.ae2nobyproduct.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CommonMod.MOD_ID)
public class AE2NoByProductForge {
    public AE2NoByProductForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Submit our event bus to let architectury register our content at the right time.
        EventBuses.registerModEventBus(CommonMod.MOD_ID, modEventBus);

        // Supply the loader-specific settings + per-player toggle persistence to the shared core.
        ByproductConfig.install(new ForgeByproductConfig());

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        Network.register();
        ModItems.register(modEventBus);

        CommonMod.init();
    }
}

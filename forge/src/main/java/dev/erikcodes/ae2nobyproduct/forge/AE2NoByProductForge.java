package dev.erikcodes.ae2nobyproduct.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.config.Config;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
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

        // Global config in the standard config/ folder (config/ae2nobyproduct.toml), like most
        // mods, instead of Forge's per-world serverconfig/. COMMON = one file per install; the
        // server reads it and stays authoritative, syncing the bits clients need via ModNetworking
        // (Forge does not auto-sync COMMON configs, but we do our own sync). Mirrors Fabric's
        // config/ae2nobyproduct.json.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, CommonMod.MOD_ID + ".toml");

        // Items + networking are registered loader-agnostically inside CommonMod.init() via
        // Architectury. EventBuses.registerModEventBus above gives Architectury the Forge bus it
        // needs for the deferred item registration that init() kicks off.
        CommonMod.init();
    }
}

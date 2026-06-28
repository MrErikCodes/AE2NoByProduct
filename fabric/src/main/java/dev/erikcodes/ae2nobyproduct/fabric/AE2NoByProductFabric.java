package dev.erikcodes.ae2nobyproduct.fabric;

import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.core.ByproductConfig;
import net.fabricmc.api.ModInitializer;

public class AE2NoByProductFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // Supply Fabric's loader-specific settings + per-player toggle persistence to the shared core,
        // mirroring what AE2NoByProductForge does with ForgeByproductConfig. With this installed the
        // shared handlers get full feature parity on Fabric (config + per-player toggle).
        ByproductConfig.install(new FabricByproductConfig());
        CommonMod.init();
    }
}

package dev.erikcodes.ae2nobyproduct;

import com.mojang.logging.LogUtils;
import dev.architectury.utils.Env;
import dev.architectury.utils.EnvExecutor;
import dev.erikcodes.ae2nobyproduct.command.ByproductCommands;
import dev.erikcodes.ae2nobyproduct.network.ModNetworking;
import dev.erikcodes.ae2nobyproduct.registry.ModItems;
import org.slf4j.Logger;

/**
 * Shared mod bootstrap, invoked by the loader entry point {@link AE2NoByproduct} once the platform's
 * mod event bus (if any) is ready.
 */
public final class CommonMod {
    public static final String MOD_ID = "ae2nobyproduct";
    public static final Logger LOGGER = LogUtils.getLogger();

    private CommonMod() {}

    public static void init() {
        LOGGER.info("AE2 No Byproduct (common) initializing");
        // Items + creative-tab entry, registered loader-agnostically via Architectury.
        ModItems.init();
        // Networking is loader-agnostic (Architectury NetworkManager). The C2S receiver is safe on
        // both sides; the S2C receiver is client-only (it maps to ClientPlayNetworking on Fabric, which
        // is absent on a dedicated server), so it is registered behind an Env.CLIENT guard.
        ModNetworking.init();
        EnvExecutor.runInEnv(Env.CLIENT, () -> ModNetworking::initClient);
        // The /ae2nobyproduct operator commands (inspect + strip-all), registered loader-agnostically
        // via Architectury. Safe to register on both physical sides.
        ByproductCommands.register();
    }
}

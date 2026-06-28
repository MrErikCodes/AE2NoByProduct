package dev.erikcodes.ae2nobyproduct.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.erikcodes.ae2nobyproduct.CommonMod;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Fabric's loader-specific config leaf. Vanilla/Fabric has no {@code ForgeConfigSpec}, so the same
 * options are stored as a small JSON file in the config dir. Defaults match the Forge config exactly,
 * so the shared handlers (reached through the {@code ByproductConfig} seam) behave identically on
 * both loaders.
 */
public final class FabricConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static FabricConfig instance;

    public boolean enableFeature = true;
    public boolean allowPlayerToggle = true;
    public boolean defaultStrip = false;
    public boolean consumeOnUse = false;
    public boolean showMessages = true;

    private FabricConfig() {}

    public static FabricConfig get() {
        if (instance == null) {
            instance = load();
        }
        return instance;
    }

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(CommonMod.MOD_ID + ".json");
    }

    private static FabricConfig load() {
        FabricConfig cfg = new FabricConfig(); // primitive field defaults
        Path p = path();
        if (Files.exists(p)) {
            try {
                // Overlay only the keys actually present, so a partial/old file keeps the defaults
                // for any missing option instead of silently reading false.
                JsonObject o = GSON.fromJson(Files.readString(p), JsonObject.class);
                if (o != null) {
                    cfg.enableFeature = bool(o, "enableFeature", cfg.enableFeature);
                    cfg.allowPlayerToggle = bool(o, "allowPlayerToggle", cfg.allowPlayerToggle);
                    cfg.defaultStrip = bool(o, "defaultStrip", cfg.defaultStrip);
                    cfg.consumeOnUse = bool(o, "consumeOnUse", cfg.consumeOnUse);
                    cfg.showMessages = bool(o, "showMessages", cfg.showMessages);
                }
            } catch (IOException | RuntimeException e) {
                // Leave the user's broken-but-recoverable file untouched so they can fix it; run with
                // defaults this session rather than clobbering it with default values.
                CommonMod.LOGGER.warn("Could not read {}.json; using defaults this run, file left as-is", CommonMod.MOD_ID, e);
                return cfg;
            }
            cfg.save(); // parsed OK: rewrite to backfill any keys missing from an older file
        } else {
            cfg.save(); // no file yet: materialise one with the defaults
        }
        return cfg;
    }

    private static boolean bool(JsonObject o, String key, boolean def) {
        return (o.has(key) && o.get(key).isJsonPrimitive()) ? o.get(key).getAsBoolean() : def;
    }

    public void save() {
        try {
            Files.writeString(path(), GSON.toJson(this));
        } catch (IOException e) {
            CommonMod.LOGGER.warn("Could not write {}.json", CommonMod.MOD_ID, e);
        }
    }
}

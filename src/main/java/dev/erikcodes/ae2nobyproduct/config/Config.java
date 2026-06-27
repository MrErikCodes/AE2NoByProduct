package dev.erikcodes.ae2nobyproduct.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class Config {
    public static final ForgeConfigSpec SPEC;
    private static final ForgeConfigSpec.BooleanValue ENABLE_FEATURE;
    private static final ForgeConfigSpec.BooleanValue ALLOW_PLAYER_TOGGLE;
    private static final ForgeConfigSpec.BooleanValue DEFAULT_STRIP;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
        ENABLE_FEATURE = b.comment("Master switch. If false the mod does nothing and no button is shown.")
                .define("enableFeature", true);
        ALLOW_PLAYER_TOGGLE = b.comment("If false, players cannot toggle; defaultStrip is forced for everyone and the button is hidden.")
                .define("allowPlayerToggle", true);
        DEFAULT_STRIP = b.comment("Default for new players; also the forced value when allowPlayerToggle=false.")
                .define("defaultStrip", false);
        SPEC = b.build();
    }
    private Config() {}
    public static boolean enableFeature() { return ENABLE_FEATURE.get(); }
    public static boolean allowPlayerToggle() { return ALLOW_PLAYER_TOGGLE.get(); }
    public static boolean defaultStrip() { return DEFAULT_STRIP.get(); }
}

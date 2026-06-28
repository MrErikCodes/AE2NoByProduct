package dev.erikcodes.ae2nobyproduct.core;

import net.minecraft.world.entity.player.Player;

/**
 * Loader-agnostic access to the settings the byproduct-stripping logic needs.
 *
 * <p>The decision logic ({@link EffectiveState} / {@link ByproductService}) lives in
 * {@code common} and is shared by Forge and Fabric. The actual source of these values is
 * loader-specific, so each platform installs a {@link Provider}:
 * <ul>
 *   <li><b>Forge</b> installs {@code ForgeByproductConfig}, backed by its {@code ForgeConfigSpec}
 *       config and a per-player persisted-NBT toggle store.</li>
 *   <li><b>Fabric</b> installs {@code FabricByproductConfig}, backed by a JSON config and a
 *       per-player toggle stored via the persistence mixins.</li>
 * </ul>
 * Until a platform installs its provider, {@link #DEFAULT} is used (feature on, no per-player
 * toggle, strip by default).
 *
 * <p>Per-player toggle access ({@link Provider#savedState}/{@link Provider#setSavedState}) is part
 * of this interface because the persistence mechanism is loader-specific: Forge uses
 * {@code Player.getPersistentData()}, which has no vanilla/Fabric equivalent, so each platform
 * supplies its own.
 */
public final class ByproductConfig {

    public interface Provider {
        boolean enableFeature();
        boolean allowPlayerToggle();
        boolean defaultStrip();
        /** Whether the Byproduct Remover item is consumed (shrinks by 1) on a successful clean. */
        boolean consumeOnUse();
        /** Whether the Byproduct Remover item sends chat feedback when used. */
        boolean showMessages();
        /** Per-player saved toggle. Only consulted when {@link #allowPlayerToggle()} is true. */
        boolean savedState(Player player, boolean def);
        void setSavedState(Player player, boolean value);
    }

    /** Fallback used until a platform installs its own {@link Provider} during init. */
    public static final Provider DEFAULT = new Provider() {
        @Override public boolean enableFeature() { return true; }
        @Override public boolean allowPlayerToggle() { return false; }
        @Override public boolean defaultStrip() { return true; }
        @Override public boolean consumeOnUse() { return false; }
        @Override public boolean showMessages() { return true; }
        @Override public boolean savedState(Player player, boolean def) { return def; }
        @Override public void setSavedState(Player player, boolean value) { /* no persistence */ }
    };

    private static volatile Provider provider = DEFAULT;

    private ByproductConfig() {}

    /** Called once per platform during mod init to supply loader-specific settings. */
    public static void install(Provider p) {
        provider = (p != null) ? p : DEFAULT;
    }

    public static Provider get() {
        return provider;
    }
}

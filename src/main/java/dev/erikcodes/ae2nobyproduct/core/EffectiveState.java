package dev.erikcodes.ae2nobyproduct.core;

public final class EffectiveState {
    private EffectiveState() {}
    public static boolean compute(boolean enableFeature, boolean allowToggle, boolean defaultStrip, boolean savedState) {
        if (!enableFeature) return false;
        if (!allowToggle) return defaultStrip;
        return savedState;
    }
}

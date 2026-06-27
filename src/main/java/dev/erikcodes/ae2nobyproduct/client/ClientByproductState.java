package dev.erikcodes.ae2nobyproduct.client;

public final class ClientByproductState {
    public static boolean effectiveState = false;
    public static boolean featureEnabled = false;
    public static boolean allowToggle = false;
    private ClientByproductState() {}
    public static void update(boolean eff, boolean fe, boolean at) {
        effectiveState = eff; featureEnabled = fe; allowToggle = at;
    }
    public static boolean showButton() { return featureEnabled && allowToggle; }
}

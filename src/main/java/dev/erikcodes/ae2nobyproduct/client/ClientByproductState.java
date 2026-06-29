package dev.erikcodes.ae2nobyproduct.client;

/**
 * Client-side cache of the server's effective state + config gates for the local player, updated by
 * the S2C sync packet (see {@code ModNetworking#initClient}). Pure POJO with no client-only
 * Minecraft references, so it lives in {@code common} and is read by the toolbar button.
 */
public final class ClientByproductState {
    public static boolean effectiveState = false;
    public static boolean featureEnabled = false;
    public static boolean allowToggle = false;

    /** Set by the toolbar button so it can refresh its icon/visibility/tooltip when a sync arrives.
     *  A plain Runnable keeps this class free of client-only references. */
    public static Runnable onChange;

    private ClientByproductState() {}

    public static void update(boolean eff, boolean fe, boolean at) {
        effectiveState = eff; featureEnabled = fe; allowToggle = at;
        if (onChange != null) onChange.run();
    }
    public static boolean showButton() { return featureEnabled && allowToggle; }
}

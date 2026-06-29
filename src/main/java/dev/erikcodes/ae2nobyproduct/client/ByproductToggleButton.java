package dev.erikcodes.ae2nobyproduct.client;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import dev.erikcodes.ae2nobyproduct.network.ModNetworking;
import net.minecraft.network.chat.Component;

// Extends AE2's IconButton (itself a net.minecraft...Button) so it can be handed to
// AEBaseScreen#addToLeftToolbar (signature <B extends Button>) and renders with the native AE2
// toolbar frame + an icon, matching the adjacent left-toolbar buttons.
//
// IconButton draws everything in renderWidget via getIcon(); both AbstractWidget#render and
// IconButton#renderWidget are final on 1.21+, so we never touch the render path. Instead the button
// refreshes its icon/visibility/tooltip reactively whenever the server state syncs in (see
// ClientByproductState#onChange), which also covers a sync arriving after the screen was built.
public class ByproductToggleButton extends IconButton {

    public ByproductToggleButton() {
        // No-op OnPress: we override onPress() below. Keeps the required no-arg ctor.
        super(b -> {});
        ClientByproductState.onChange = this::refresh;
        refresh();
    }

    /** Pull the latest synced state into this widget's visibility + tooltip. */
    private void refresh() {
        // Visibility is config-driven and may sync in after this screen was built.
        this.visible = ClientByproductState.showButton();
        // IconButton implements ITooltip and renders the tooltip from getMessage(), so setting the
        // message is AE2's native tooltip path.
        setMessage(Component.translatable(
            ClientByproductState.effectiveState ? "tooltip.ae2nobyproduct.on" : "tooltip.ae2nobyproduct.off"));
    }

    // Bright green check when removal is ON, red cross when OFF; reads as active/inactive.
    @Override
    protected Icon getIcon() {
        return ClientByproductState.effectiveState ? Icon.VALID : Icon.INVALID;
    }

    @Override
    public void onPress() {
        // Optimistic local flip; the server echoes the authoritative state back via sync, which calls
        // refresh() again and corrects this if the toggle was rejected.
        ClientByproductState.effectiveState = !ClientByproductState.effectiveState;
        ModNetworking.sendSetToggle(ClientByproductState.effectiveState);
        refresh();
    }
}

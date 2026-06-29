package dev.erikcodes.ae2nobyproduct.client;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import dev.erikcodes.ae2nobyproduct.network.ModNetworking;
import net.minecraft.network.chat.Component;

// Extends AE2's IconButton (itself a net.minecraft...Button) so it can be handed to
// AEBaseScreen#addToLeftToolbar (signature <B extends Button>) and renders with the native AE2
// toolbar frame + an icon, matching the adjacent left-toolbar buttons.
//
// Visibility is config-driven and arrives via a server sync that can land before or after this screen
// is built, so it must be (re)applied robustly. AE2's VerticalButtonBar re-lays-out every frame and
// skips buttons whose `visible` is false, so the field just has to be kept correct:
//   - 1.20.1: AbstractWidget#render is overridable, so we refresh every frame (timing-proof).
//   - 1.21+:  render and IconButton#renderWidget are both final, so we instead refresh reactively
//             whenever the state syncs in (ClientByproductState#onChange).
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

    //? if <1.21 {
    // 1.20.1: refresh visibility/tooltip every frame so the button reliably appears the moment the
    // config syncs in, independent of packet timing. (super.render delegates to AE2's icon drawing.)
    @Override
    public void render(net.minecraft.client.gui.GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        refresh();
        super.render(g, mouseX, mouseY, partialTick);
    }
    //?}
}

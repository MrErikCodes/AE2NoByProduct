package dev.erikcodes.ae2nobyproduct.client;

import appeng.client.gui.Icon;
import appeng.client.gui.widgets.IconButton;
import dev.erikcodes.ae2nobyproduct.network.C2SSetByproductRemoval;
import dev.erikcodes.ae2nobyproduct.network.Network;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

// Extends AE2's IconButton (itself a net.minecraft...Button) so it can be handed to
// AEBaseScreen#addToLeftToolbar (signature <B extends Button>) and renders with the
// native AE2 toolbar frame + an icon, matching the adjacent left-toolbar buttons.
// IconButton handles all drawing in renderWidget via the chosen getIcon(); we must NOT
// override renderWidget. Position is (0,0); AE2's VerticalButtonBar assigns x/y each frame.
public class ByproductToggleButton extends IconButton {
    private boolean lastKnownState;

    public ByproductToggleButton() {
        // No-op OnPress: we override onPress() below. Keeps the required no-arg ctor.
        super(b -> {});
        lastKnownState = ClientByproductState.effectiveState;
        refreshTooltip();
    }

    // IconButton implements ITooltip and renders the tooltip from getTooltipMessage(),
    // which returns getMessage(); so setting the message is AE2's native tooltip path.
    private void refreshTooltip() {
        setMessage(Component.translatable(
            ClientByproductState.effectiveState ? "tooltip.ae2nobyproduct.on" : "tooltip.ae2nobyproduct.off"));
    }

    // Bright green check when removal is ON, red cross when OFF — reads as active/inactive.
    @Override
    protected Icon getIcon() {
        return ClientByproductState.effectiveState ? Icon.VALID : Icon.INVALID;
    }

    @Override
    public void onPress() {
        boolean nv = !ClientByproductState.effectiveState;
        ClientByproductState.effectiveState = nv;
        lastKnownState = nv;
        Network.CHANNEL.sendToServer(new C2SSetByproductRemoval(nv));
        refreshTooltip();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float pt) {
        // Visibility is config-driven and may sync in after this screen was built.
        this.visible = ClientByproductState.showButton();
        if (lastKnownState != ClientByproductState.effectiveState) {
            lastKnownState = ClientByproductState.effectiveState;
            refreshTooltip();
        }
        super.render(g, mouseX, mouseY, pt);
    }
}

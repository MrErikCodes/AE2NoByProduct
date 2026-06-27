package dev.erikcodes.ae2nobyproduct.client;

import dev.erikcodes.ae2nobyproduct.network.C2SSetByproductRemoval;
import dev.erikcodes.ae2nobyproduct.network.Network;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ByproductToggleButton extends AbstractButton {
    public ByproductToggleButton(int x, int y) {
        super(x, y, 16, 16, Component.empty());
        refreshTooltip();
    }
    private void refreshTooltip() {
        setTooltip(Tooltip.create(Component.translatable(
            ClientByproductState.effectiveState ? "tooltip.ae2nobyproduct.on" : "tooltip.ae2nobyproduct.off")));
    }
    @Override public void onPress() {
        boolean nv = !ClientByproductState.effectiveState;
        ClientByproductState.effectiveState = nv;
        Network.CHANNEL.sendToServer(new C2SSetByproductRemoval(nv));
        refreshTooltip();
    }
    @Override protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float pt) {
        refreshTooltip();
        g.fill(getX(), getY(), getX() + 16, getY() + 16, 0xFF000000);
        g.fill(getX() + 1, getY() + 1, getX() + 15, getY() + 15, 0xFF303030);
        int fg = ClientByproductState.effectiveState ? 0xFF40C040 : 0xFF707070;
        g.fill(getX() + 4, getY() + 4, getX() + 12, getY() + 12, fg);
        if (isHovered()) g.fill(getX(), getY(), getX() + 16, getY() + 16, 0x33FFFFFF);
    }
    @Override protected void updateWidgetNarration(NarrationElementOutput o) { defaultButtonNarrationText(o); }
}

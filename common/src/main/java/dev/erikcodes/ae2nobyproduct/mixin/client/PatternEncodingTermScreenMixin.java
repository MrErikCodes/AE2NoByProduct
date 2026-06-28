package dev.erikcodes.ae2nobyproduct.mixin.client;

import appeng.client.gui.me.items.PatternEncodingTermScreen;
import dev.erikcodes.ae2nobyproduct.client.ByproductToggleButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Adds our toggle button to AE2's native left toolbar, once, in the screen
// constructor. AEBaseScreen creates its `verticalToolbar` (a VerticalButtonBar)
// in its constructor and keeps a persistent button list that init()/resize only
// re-registers and re-positions, so a single constructor-time add appears once,
// never duplicates, and survives resizes natively.
//
// addToLeftToolbar is invoked through AEBaseScreenInvoker (an @Invoker on the
// declaring superclass) rather than a @Shadow on this subclass, because Mixin
// cannot resolve a @Shadow of a method inherited from another mod's superclass.
//
// remap = false: PatternEncodingTermScreen / its constructor belong to AE2 and
// keep stable names dev<->prod, so the Mixin AP must not map them to searge.
@Mixin(value = PatternEncodingTermScreen.class, remap = false)
public abstract class PatternEncodingTermScreenMixin {

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void ae2nobyproduct$addToggleButton(CallbackInfo ci) {
        ((AEBaseScreenInvoker) (Object) this).ae2nobyproduct$addToLeftToolbar(new ByproductToggleButton());
    }
}

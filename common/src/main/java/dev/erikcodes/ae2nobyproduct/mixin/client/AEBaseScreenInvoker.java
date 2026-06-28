package dev.erikcodes.ae2nobyproduct.mixin.client;

import appeng.client.gui.AEBaseScreen;
import net.minecraft.client.gui.components.Button;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

// @Invoker targeting AEBaseScreen directly, the class that DECLARES
// `protected final <B extends Button> B addToLeftToolbar(B)`. A @Shadow on the
// PatternEncodingTermScreen subclass fails at apply time ("not located in target
// class") because Mixin won't resolve a method inherited from a different mod's
// superclass. Targeting the declaring class with an @Invoker is the standard fix.
// remap = false: addToLeftToolbar is AE2's own method (stable name dev<->prod);
// the MC `Button` in the descriptor is handled by ForgeGradle reobf at bytecode level.
@Mixin(value = AEBaseScreen.class, remap = false)
public interface AEBaseScreenInvoker {
    @Invoker("addToLeftToolbar")
    Button ae2nobyproduct$addToLeftToolbar(Button button);
}

package dev.erikcodes.ae2nobyproduct.mixin;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.erikcodes.ae2nobyproduct.core.ByproductService;
import dev.erikcodes.ae2nobyproduct.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// This mixin lives in :common and is applied on BOTH Forge and Fabric.
//
// Class- and @Redirect-level remap = false: the target class (PatternEncodingTermMenu) and the
// injected method name (encodeProcessingPattern) belong to AE2, whose names are stable across
// loaders and absent from the Minecraft mappings; trying to remap them would fail the build.
//
// The @At INVOKE target, however, is remap = true: its descriptor contains a Minecraft type
// (ItemStack) whose runtime name differs per loader (intermediary class_1799 on Fabric, named on
// Forge). With remap = true the Mixin AP records a refmap entry that rewrites only that MC type
// per platform while leaving the AE2 owner/method untouched, making the injector resolve on both.
@Mixin(value = PatternEncodingTermMenu.class, remap = false)
public abstract class PatternEncodingTermMenuMixin {

    // AE2 15.4 (1.20.1) passes the in/out stacks to PatternDetailsHelper.encodeProcessingPattern as
    // GenericStack[]; AE2 19.x (1.21.1) passes them as List<GenericStack>. Only the @At descriptor, the
    // redirect parameter types, and the "keep one output" line differ; the rest is shared.
    @Redirect(
        method = "encodeProcessingPattern",
        remap = false,
        at = @At(
            value = "INVOKE",
            //? if >=1.21 {
            /*target = "Lappeng/api/crafting/PatternDetailsHelper;encodeProcessingPattern(Ljava/util/List;Ljava/util/List;)Lnet/minecraft/world/item/ItemStack;",
            *///?} else {
            target = "Lappeng/api/crafting/PatternDetailsHelper;encodeProcessingPattern([Lappeng/api/stacks/GenericStack;[Lappeng/api/stacks/GenericStack;)Lnet/minecraft/world/item/ItemStack;",
            //?}
            remap = true
        )
    )
    //? if >=1.21 {
    /*private ItemStack ae2nobyproduct$stripByproducts(java.util.List<GenericStack> inputs, java.util.List<GenericStack> outputs) {
    *///?} else {
    private ItemStack ae2nobyproduct$stripByproducts(GenericStack[] inputs, GenericStack[] outputs) {
    //?}
        Player player = ((AEBaseMenu) (Object) this).getPlayer();
        if (player != null && ByproductService.shouldStrip(player)) {
            // Outputs are sparse: slots can be null, and the primary output is not necessarily slot 0.
            // Keep the first non-null one, matching ByproductRemoverItem's cleanup so live encoding and
            // the tool agree on which output survives.
            for (GenericStack output : outputs) {
                if (output != null) {
                    //? if >=1.21 {
                    /*outputs = java.util.List.of(output);
                    *///?} else {
                    outputs = new GenericStack[] { output };
                    //?}
                    break;
                }
            }
        }
        return PatternDetailsHelper.encodeProcessingPattern(inputs, outputs);
    }

    // Server-side sync trigger, loader-agnostic replacement for the Forge PlayerContainerEvent.Open
    // handler: when this menu is built for a ServerPlayer, push their effective state to the client so
    // the toolbar button renders correctly the moment the screen opens.
    //
    // method = "<init>" with no descriptor matches BOTH constructors. The public 3-arg ctor delegates
    // via this(...) to the 5-arg base ctor, and add-on terminals (wireless pattern terminals, etc.)
    // super(...) into that same base ctor. Targeting by name therefore covers every terminal variant
    // without binding to a Minecraft-typed descriptor (which would need per-loader refmap remapping).
    // The vanilla terminal fires twice (once per ctor in the chain); the packet is idempotent, so the
    // duplicate is harmless. On the physical client getPlayer() is the LocalPlayer, so nothing is sent.
    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void ae2nobyproduct$syncOnOpen(CallbackInfo ci) {
        if (((AEBaseMenu) (Object) this).getPlayer() instanceof ServerPlayer serverPlayer) {
            ModNetworking.sendSyncState(serverPlayer);
        }
    }
}

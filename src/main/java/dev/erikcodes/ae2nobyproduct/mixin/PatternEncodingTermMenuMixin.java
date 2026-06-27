package dev.erikcodes.ae2nobyproduct.mixin;

import appeng.api.crafting.PatternDetailsHelper;
import appeng.api.stacks.GenericStack;
import appeng.menu.AEBaseMenu;
import appeng.menu.me.items.PatternEncodingTermMenu;
import dev.erikcodes.ae2nobyproduct.core.ByproductService;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// remap = false: every target here belongs to AE2 (another mod), whose member
// names are identical in dev and production, so the Mixin annotation processor
// must not try to map them to Minecraft searge names (which would fail the build).
@Mixin(value = PatternEncodingTermMenu.class, remap = false)
public abstract class PatternEncodingTermMenuMixin {

    @Redirect(
        method = "encodeProcessingPattern",
        remap = false,
        at = @At(
            value = "INVOKE",
            target = "Lappeng/api/crafting/PatternDetailsHelper;encodeProcessingPattern([Lappeng/api/stacks/GenericStack;[Lappeng/api/stacks/GenericStack;)Lnet/minecraft/world/item/ItemStack;",
            remap = false
        )
    )
    private ItemStack ae2nobyproduct$stripByproducts(GenericStack[] inputs, GenericStack[] outputs) {
        Player player = ((AEBaseMenu) (Object) this).getPlayer();
        if (player != null && ByproductService.shouldStrip(player) && outputs.length > 0 && outputs[0] != null) {
            outputs = new GenericStack[] { outputs[0] };
        }
        return PatternDetailsHelper.encodeProcessingPattern(inputs, outputs);
    }
}

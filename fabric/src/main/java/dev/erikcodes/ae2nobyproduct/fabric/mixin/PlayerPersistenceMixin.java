package dev.erikcodes.ae2nobyproduct.fabric.mixin;

import dev.erikcodes.ae2nobyproduct.CommonMod;
import dev.erikcodes.ae2nobyproduct.fabric.persistence.ByproductToggleAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric per-player persistence. Vanilla has no Forge {@code getPersistentData()}, so the toggle is
 * stored as a field on {@code Player} and (de)serialised under our own NBT sub-tag in the vanilla
 * add/readAdditionalSaveData hooks (relog + server restart). Death/respawn copying is handled by
 * {@link ServerPlayerRespawnMixin}, because the respawned entity is rebuilt, not disk-loaded.
 *
 * <p>Targets vanilla Minecraft methods, so remap is left at its default (true): the mojmap names are
 * rewritten to the per-platform runtime names via the generated refmap.
 */
@Mixin(Player.class)
public abstract class PlayerPersistenceMixin implements ByproductToggleAccess {
    @Unique private boolean ae2nobyproduct$toggle = false;
    @Unique private boolean ae2nobyproduct$present = false;

    @Override public boolean ae2nobyproduct$hasToggle() { return ae2nobyproduct$present; }
    @Override public boolean ae2nobyproduct$getToggle() { return ae2nobyproduct$toggle; }
    @Override public void ae2nobyproduct$setToggle(boolean value) {
        ae2nobyproduct$toggle = value;
        ae2nobyproduct$present = true;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void ae2nobyproduct$save(CompoundTag tag, CallbackInfo ci) {
        if (ae2nobyproduct$present) {
            CompoundTag sub = new CompoundTag();
            sub.putBoolean("strip", ae2nobyproduct$toggle);
            tag.put(CommonMod.MOD_ID, sub);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void ae2nobyproduct$read(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(CommonMod.MOD_ID)) {
            CompoundTag sub = tag.getCompound(CommonMod.MOD_ID);
            if (sub.contains("strip")) {
                ae2nobyproduct$toggle = sub.getBoolean("strip");
                ae2nobyproduct$present = true;
            }
        }
    }
}

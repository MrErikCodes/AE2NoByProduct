package dev.erikcodes.ae2nobyproduct.fabric.mixin;

import dev.erikcodes.ae2nobyproduct.fabric.persistence.ByproductToggleAccess;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Carries the per-player byproduct toggle across death. On respawn the server builds a fresh
 * {@code ServerPlayer} and calls {@code restoreFrom(...)} instead of loading from disk, so
 * {@link PlayerPersistenceMixin}'s read hook does not run; without this copy the toggle would reset
 * every time the player dies. (Forge gets this for free via the PERSISTED_NBT_TAG it copies on clone.)
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerRespawnMixin {
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void ae2nobyproduct$copyToggle(ServerPlayer oldPlayer, boolean keepEverything, CallbackInfo ci) {
        ByproductToggleAccess from = (ByproductToggleAccess) oldPlayer;
        if (from.ae2nobyproduct$hasToggle()) {
            ((ByproductToggleAccess) (Object) this).ae2nobyproduct$setToggle(from.ae2nobyproduct$getToggle());
        }
    }
}

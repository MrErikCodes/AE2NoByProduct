package dev.erikcodes.ae2nobyproduct.fabric.persistence;

/**
 * Duck interface implemented on every {@code Player} by {@code PlayerPersistenceMixin}. Lets the
 * Fabric config provider read and write the per-player byproduct toggle that the mixin stores on the
 * entity and serialises to NBT. The Forge side does not need this: it uses {@code getPersistentData()}.
 */
public interface ByproductToggleAccess {
    /** True once a value has been set or loaded for this player. */
    boolean ae2nobyproduct$hasToggle();

    boolean ae2nobyproduct$getToggle();

    void ae2nobyproduct$setToggle(boolean value);
}

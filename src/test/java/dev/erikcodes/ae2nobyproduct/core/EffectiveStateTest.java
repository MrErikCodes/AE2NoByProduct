package dev.erikcodes.ae2nobyproduct.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EffectiveStateTest {
    @Test void masterOffAlwaysFalse() {
        assertFalse(EffectiveState.compute(false, true, true, true));
    }
    @Test void toggleDisallowedUsesDefault() {
        assertTrue(EffectiveState.compute(true, false, true, false));
        assertFalse(EffectiveState.compute(true, false, false, true));
    }
    @Test void toggleAllowedUsesSaved() {
        assertTrue(EffectiveState.compute(true, true, false, true));
        assertFalse(EffectiveState.compute(true, true, true, false));
    }
}

package com.ailimittrainer.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for LimitMonitor and LimitConfig.
 */
public class LimitMonitorTest {

    @Test
    public void testConfigWithPositiveValues() {
        LimitConfig config = new LimitConfig(512, 10000, 1000, true);
        assertEquals(512, config.getMaxMemoryMB());
        assertEquals(10000, config.getMaxTimeMs());
        assertEquals(1000, config.getMaxTokens());
        assertTrue(config.isStrictMode());
    }

    @Test
    public void testConfigRejectsNonPositiveMemory() {
        assertThrows(IllegalArgumentException.class, () -> {
            new LimitConfig(0, 1000, 100, false);
        });
    }

    @Test
    public void testMonitorNoViolationOnStart() throws LimitMonitor.LimitViolationException {
        LimitConfig config = new LimitConfig(1024, 50000, 5000, true);
        LimitMonitor monitor = new LimitMonitor(config);
        // Should not throw immediately
        monitor.checkLimits();
    }

    @Test
    public void testTokenLimitViolationInStrictMode() {
        LimitConfig config = new LimitConfig(1024, 60000, 5, true);
        LimitMonitor monitor = new LimitMonitor(config);
        // Generate 6 tokens to exceed limit of 5
        for (int i = 0; i < 6; i++) {
            monitor.recordToken();
        }
        assertThrows(LimitMonitor.LimitViolationException.class, () -> {
            monitor.checkLimits();
        });
    }

    @Test
    public void testSoftModeDoesNotThrow() {
        LimitConfig config = new LimitConfig(1024, 60000, 2, false);
        LimitMonitor monitor = new LimitMonitor(config);
        // Exceed token limit in soft mode
        for (int i = 0; i < 5; i++) {
            monitor.recordToken();
        }
        // Should not throw exception
        assertDoesNotThrow(() -> monitor.checkLimits());
    }
}

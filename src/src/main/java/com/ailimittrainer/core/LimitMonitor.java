package com.ailimittrainer.core;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * Monitors resource usage during AI inference and enforces configured limits.
 * Uses JMX memory beans and system time for real-time tracking.
 */
public class LimitMonitor {

    private final LimitConfig config;
    private final MemoryMXBean memoryBean;
    private final long startTime;
    private int tokensGenerated;

    /**
     * Creates a new monitor bound to given configuration.
     *
     * @param config the limit configuration to enforce
     */
    public LimitMonitor(LimitConfig config) {
        this.config = config;
        this.memoryBean = ManagementFactory.getMemoryMXBean();
        this.startTime = System.currentTimeMillis();
        this.tokensGenerated = 0;
    }

    /**
     * Checks all resource limits. Throws or logs based on strict mode.
     *
     * @throws LimitViolationException if strict mode and any limit exceeded
     */
    public void checkLimits() throws LimitViolationException {
        checkMemoryLimit();
        checkTimeLimit();
        checkTokenLimit();
    }

    /**
     * Records generation of a token for tracking.
     */
    public void recordToken() {
        tokensGenerated++;
    }

    /**
     * Returns the current token count.
     */
    public int getTokensGenerated() {
        return tokensGenerated;
    }

    private void checkMemoryLimit() throws LimitViolationException {
        MemoryUsage heapUsage = memoryBean.getHeapMemoryUsage();
        long usedMB = heapUsage.getUsed() / (1024 * 1024);
        if (usedMB > config.getMaxMemoryMB()) {
            String msg = "Memory limit exceeded: " + usedMB + "MB used, max " + config.getMaxMemoryMB() + "MB";
            handleViolation(msg);
        }
    }

    private void checkTimeLimit() throws LimitViolationException {
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > config.getMaxTimeMs()) {
            String msg = "Time limit exceeded: " + elapsed + "ms elapsed, max " + config.getMaxTimeMs() + "ms";
            handleViolation(msg);
        }
    }

    private void checkTokenLimit() throws LimitViolationException {
        if (tokensGenerated > config.getMaxTokens()) {
            String msg = "Token limit exceeded: " + tokensGenerated + " tokens, max " + config.getMaxTokens();
            handleViolation(msg);
        }
    }

    private void handleViolation(String message) throws LimitViolationException {
        if (config.isStrictMode()) {
            throw new LimitViolationException(message);
        } else {
            System.err.println("[WARN] " + message);
        }
    }

    /**
     * Exception thrown when a limit is violated in strict mode.
     */
    public static class LimitViolationException extends Exception {
        public LimitViolationException(String message) {
            super(message);
        }
    }
}

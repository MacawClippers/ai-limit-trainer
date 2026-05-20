package com.ailimittrainer.core;

/**
 * Configuration class defining resource limits for AI model inference.
 * This includes memory, time, and token limits to prevent runaway execution.
 */
public class LimitConfig {

    /** Maximum memory usage in megabytes */
    private final long maxMemoryMB;

    /** Maximum inference time in milliseconds */
    private final long maxTimeMs;

    /** Maximum number of tokens generated */
    private final int maxTokens;

    /** Whether to enforce limits strictly (throw exception) or softly (log warning) */
    private final boolean strictMode;

    /**
     * Constructs a LimitConfig with specified bounds.
     *
     * @param maxMemoryMB maximum memory in MB (must be > 0)
     * @param maxTimeMs   maximum time in ms (must be > 0)
     * @param maxTokens   maximum tokens (must be > 0)
     * @param strictMode  if true, violations throw exceptions; otherwise warnings
     */
    public LimitConfig(long maxMemoryMB, long maxTimeMs, int maxTokens, boolean strictMode) {
        if (maxMemoryMB <= 0 || maxTimeMs <= 0 || maxTokens <= 0) {
            throw new IllegalArgumentException("All limits must be positive values.");
        }
        this.maxMemoryMB = maxMemoryMB;
        this.maxTimeMs = maxTimeMs;
        this.maxTokens = maxTokens;
        this.strictMode = strictMode;
    }

    public long getMaxMemoryMB() {
        return maxMemoryMB;
    }

    public long getMaxTimeMs() {
        return maxTimeMs;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public boolean isStrictMode() {
        return strictMode;
    }

    @Override
    public String toString() {
        return "LimitConfig{" +
                "maxMemoryMB=" + maxMemoryMB +
                ", maxTimeMs=" + maxTimeMs +
                ", maxTokens=" + maxTokens +
                ", strictMode=" + strictMode +
                '}';
    }
}

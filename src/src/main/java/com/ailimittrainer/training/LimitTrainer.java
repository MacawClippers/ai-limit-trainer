package com.ailimittrainer.training;

import com.ailimittrainer.core.LimitConfig;
import com.ailimittrainer.core.LimitMonitor;

import java.util.Random;

/**
 * Simulates training an AI model to respect resource limits.
 * This class runs mock inference cycles and adjusts behavior
 * based on limit violations, teaching the model to stay within bounds.
 */
public class LimitTrainer {

    private final LimitConfig config;
    private final Random random;
    private int violationCount;
    private int totalCycles;

    /**
     * Creates a trainer with given configuration.
     *
     * @param config the limits to enforce during training
     */
    public LimitTrainer(LimitConfig config) {
        this.config = config;
        this.random = new Random(42);
        this.violationCount = 0;
        this.totalCycles = 0;
    }

    /**
     * Runs a single training cycle: simulates inference and checks limits.
     *
     * @return true if cycle completed without strict violation, false otherwise
     */
    public boolean trainCycle() {
        totalCycles++;
        LimitMonitor monitor = new LimitMonitor(config);

        // Simulate token generation (random burst)
        int tokensToGenerate = random.nextInt(20) + 1;
        try {
            for (int i = 0; i < tokensToGenerate; i++) {
                monitor.recordToken();
                // Simulate small memory allocation (1KB per token)
                byte[] dummy = new byte[1024];
                // Check limits after each token
                monitor.checkLimits();
                // Simulate processing time (1-5ms per token)
                Thread.sleep(random.nextInt(5) + 1);
            }
        } catch (LimitMonitor.LimitViolationException e) {
            violationCount++;
            System.out.println("[TRAIN] Violation in cycle " + totalCycles + ": " + e.getMessage());
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    /**
     * Runs multiple training cycles to adapt the model.
     *
     * @param cycles number of cycles to run
     * @return summary string of training results
     */
    public String train(int cycles) {
        int successful = 0;
        for (int i = 0; i < cycles; i++) {
            if (trainCycle()) {
                successful++;
            }
        }
        return String.format(
                "Training complete: %d/%d cycles successful, %d violations (%.1f%% success rate)",
                successful, cycles, violationCount,
                (double) successful / cycles * 100
        );
    }

    public int getViolationCount() {
        return violationCount;
    }

    public int getTotalCycles() {
        return totalCycles;
    }
}

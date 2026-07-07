package com.mahefa.pathfindingfx.service.concurrent;

import com.mahefa.pathfindingfx.algorithm.step.StepType;

/**
 * Running counts of the steps rendered so far, by {@link StepType} plus a grand total. Updated on
 * the FX thread as the {@link StepPlayer} applies each step; read by the narrative (live metric +
 * summary) so it can describe progress without the algorithm having to report anything itself.
 */
public final class StepTally {

    private int total;
    private final int[] counts = new int[StepType.values().length];

    public void record(StepType type) {
        total++;
        counts[type.ordinal()]++;
    }

    public int total() {
        return total;
    }

    public int count(StepType type) {
        return counts[type.ordinal()];
    }
}

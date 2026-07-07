package com.mahefa.pathfindingfx.algorithm.step;

import java.util.Iterator;

/**
 * A lazy, pull-based source of {@link Step}s produced by an algorithm. Reusable across both
 * categories of algorithm (maze generation and pathfinding) — the {@code StepPlayer} pulls from it
 * at a speed-controlled cadence and renders each step, so the algorithm never touches the UI and
 * a slow/open-ended one still shows output immediately (nothing is precomputed).
 */
public interface Stepper extends Iterator<Step> {
}

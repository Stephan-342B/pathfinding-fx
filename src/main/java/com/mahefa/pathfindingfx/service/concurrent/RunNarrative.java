package com.mahefa.pathfindingfx.service.concurrent;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The narrative for one algorithm run, supplied by the caller (which knows the algorithm, grid and
 * start/target) so the generic {@link StepWorker} can log a story without knowing the algorithm:
 * <ul>
 *   <li>{@code header}/{@code configLine} — the {@code ▶ …} lines logged when the run starts;</li>
 *   <li>{@code activityLabel} — the live spinner label (e.g. "search" / "carving");</li>
 *   <li>{@code liveMetrics} — the animated line's metric text, derived from the live {@link StepTally};</li>
 *   <li>{@code summary} — the closing {@code ✓/✗ …} line, from the tally + elapsed seconds;</li>
 *   <li>{@code success} — whether the run succeeded (drives the ✓ vs ✗ marker).</li>
 * </ul>
 */
public record RunNarrative(
        String header,
        String configLine,
        String activityLabel,
        Function<StepTally, String> liveMetrics,
        BiFunction<StepTally, Long, String> summary,
        Predicate<StepTally> success) {
}

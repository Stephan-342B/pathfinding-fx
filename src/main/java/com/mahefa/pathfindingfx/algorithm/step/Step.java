package com.mahefa.pathfindingfx.algorithm.step;

import com.mahefa.pathfindingfx.domain.Location;

/**
 * One unit of visualization: "at this {@link Location}, this {@link StepType} happened". Pure domain
 * data — no JavaFX. {@code angle} is only meaningful for {@link StepType#PATH} (the cumulative arrow
 * rotation, in degrees, at that path cell); it is 0 for every other type.
 */
public record Step(Location location, StepType type, double angle) {

    public static Step of(Location location, StepType type) {
        return new Step(location, type, 0d);
    }

    public static Step path(Location location, double angle) {
        return new Step(location, StepType.PATH, angle);
    }
}

package com.mahefa.pathfindingfx.domain.enumerator;

public enum LaunchAnimationSpeed {

    FAST(10_000_000L),    // 10 ms
    AVERAGE(100_000_000L), // 100 ms
    SLOW(500_000_000L),    // 500 ms
    SHORTEST_PATH(40_000_000); // 40 ms

    private final long interval;

    LaunchAnimationSpeed(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }
}

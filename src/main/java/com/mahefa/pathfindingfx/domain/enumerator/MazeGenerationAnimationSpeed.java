package com.mahefa.pathfindingfx.domain.enumerator;

public enum MazeGenerationAnimationSpeed {

    FAST(5_000_000L),    // 5 ms
    AVERAGE(25_000_000L), // 25 ms
    SLOW(750_000_000L);    // 750 ms

    private final long interval;

    MazeGenerationAnimationSpeed(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }
}

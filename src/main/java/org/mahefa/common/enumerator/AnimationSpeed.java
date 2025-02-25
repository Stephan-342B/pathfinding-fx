package org.mahefa.common.enumerator;

public enum AnimationSpeed {

    FAST(10_000_000L),    // 10 ms
    AVERAGE(50_000_000L), // 50 ms
    SLOW(500_000_000L);    // 500 ms

    private final long interval;

    AnimationSpeed(long interval) {
        this.interval = interval;
    }

    public long getInterval() {
        return interval;
    }
}

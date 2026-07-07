package com.mahefa.pathfindingfx.algorithm.step;

import com.mahefa.pathfindingfx.algorithm.grid.GridModel;
import com.mahefa.pathfindingfx.domain.Location;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

/**
 * Shared superclass for all {@link Stepper}s (maze and pathfinding alike). Holds the state common to
 * every algorithm — the pure {@link GridModel} it runs against and the lazy step buffer — and
 * implements the {@link java.util.Iterator} contract on top of two small hooks:
 * <ul>
 *   <li>{@link #advance()} — produce the next one-or-more steps into the buffer;</li>
 *   <li>{@link #isComplete()} — whether the algorithm has finished.</li>
 * </ul>
 * Subclasses just express their algorithm in terms of {@code emit(...)} + {@code isComplete()};
 * the buffering, {@code hasNext}/{@code next} and laziness live here once.
 */
public abstract class AbstractStepper implements Stepper {

    protected final GridModel model;
    private final Deque<Step> buffer = new ArrayDeque<>();

    protected AbstractStepper(GridModel model) {
        this.model = model;
    }

    @Override
    public final boolean hasNext() {
        if (buffer.isEmpty() && !isComplete()) {
            advance();
        }
        return !buffer.isEmpty();
    }

    @Override
    public final Step next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return buffer.poll();
    }

    protected final void emit(Step step) {
        buffer.add(step);
    }

    protected final void emit(Location location, StepType type) {
        buffer.add(Step.of(location, type));
    }

    /** Produce the next batch of steps (at least one, unless complete). */
    protected abstract void advance();

    /** True once no more steps will ever be produced. */
    protected abstract boolean isComplete();
}

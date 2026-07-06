package org.mahefa.service.concurrent;

import javafx.animation.AnimationTimer;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Worker;

import java.util.function.Supplier;

/**
 * A {@link Worker} implementation for work that must run on the FX Application Thread
 * (e.g. per-frame mutation of scene graph {@code Node}s), rather than on a background
 * thread as {@link javafx.concurrent.Task}/{@link javafx.concurrent.Service} expect.
 * <p>
 * Subclasses drive an {@link AnimationTimer} via {@link #begin(Supplier, ReadOnlyBooleanProperty, Object)}
 * and get the standard {@link Worker.State} lifecycle and bindable properties in return.
 * <p>
 * {@code begin(...)} is deliberately not named {@code start()}: concrete subclasses expose their
 * own public no-arg {@code start()} (which builds their solver/generator, then calls {@code begin(...)}
 * to hand the resulting timer to this class) — naming both "start" would make the two easy to
 * confuse despite being unrelated overloads.
 */
public abstract class AnimationWorker<T> implements Worker<T> {

    private final ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(this, "state", State.READY);
    private final ReadOnlyBooleanWrapper running = new ReadOnlyBooleanWrapper(this, "running", false);
    private final ReadOnlyObjectWrapper<T> value = new ReadOnlyObjectWrapper<>(this, "value");
    private final ReadOnlyObjectWrapper<Throwable> exception = new ReadOnlyObjectWrapper<>(this, "exception");
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper(this, "message", "");
    private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(this, "title", "");
    private final ReadOnlyDoubleWrapper progress = new ReadOnlyDoubleWrapper(this, "progress", -1);
    private final ReadOnlyDoubleWrapper totalWork = new ReadOnlyDoubleWrapper(this, "totalWork", -1);
    private final ReadOnlyDoubleWrapper workDone = new ReadOnlyDoubleWrapper(this, "workDone", -1);
    private final ObjectProperty<Long> currentSpeed = new SimpleObjectProperty<>();

    private AnimationTimer animationTimer;
    private ReadOnlyBooleanProperty completionSignal;
    private ChangeListener<Boolean> completionListener;

    protected AnimationWorker() {
        currentSpeed.addListener((observable, oldSpeed, newSpeed) -> onSpeedChanged(newSpeed));
    }

    public void setCurrentSpeed(Long speed) {
        currentSpeed.setValue(speed);
    }

    public Long getCurrentSpeed() {
        return currentSpeed.get();
    }

    /**
     * Called whenever {@link #setCurrentSpeed(Long)} changes the speed, including while running.
     * No-op by default; subclasses override to forward the new speed to their underlying
     * solver/generator, since only they know which instance is currently active.
     */
    protected void onSpeedChanged(Long speed) {
    }

    /**
     * Starts the given {@link AnimationTimer} on the FX Application Thread and transitions
     * through {@code SCHEDULED -> RUNNING}. When {@code completionSignal} flips from
     * {@code true} to {@code false}, the timer is stopped, {@code completionValue} becomes
     * this worker's {@link #getValue()}, and the state transitions to {@code SUCCEEDED}.
     */
    protected final void begin(Supplier<AnimationTimer> timerSupplier, ReadOnlyBooleanProperty completionSignal, T completionValue) {
        if (getState() == State.RUNNING || getState() == State.SCHEDULED) {
            throw new IllegalStateException("AnimationWorker is already running");
        }

        setState(State.SCHEDULED);

        this.completionSignal = completionSignal;
        this.completionListener = (observable, wasRunning, isRunning) -> {
            if (wasRunning && !isRunning) {
                onCompleted(completionValue);
            }
        };
        completionSignal.addListener(completionListener);

        animationTimer = timerSupplier.get();
        setState(State.RUNNING);
        animationTimer.start();
    }

    private void onCompleted(T completionValue) {
        if (getState() != State.RUNNING) {
            return;
        }

        stopTimer();
        value.set(completionValue);
        setState(State.SUCCEEDED);
    }

    @Override
    public boolean cancel() {
        if (getState() != State.RUNNING && getState() != State.SCHEDULED) {
            return false;
        }

        stopTimer();
        setState(State.CANCELLED);
        return true;
    }

    private void stopTimer() {
        if (animationTimer != null) {
            animationTimer.stop();
        }

        if (completionSignal != null && completionListener != null) {
            completionSignal.removeListener(completionListener);
        }
    }

    private void setState(State newState) {
        state.set(newState);
        running.set(newState == State.RUNNING);
    }

    protected void setException(Throwable throwable) {
        exception.set(throwable);
    }

    @Override
    public State getState() {
        return state.get();
    }

    @Override
    public ReadOnlyObjectProperty<State> stateProperty() {
        return state.getReadOnlyProperty();
    }

    @Override
    public T getValue() {
        return value.get();
    }

    @Override
    public ReadOnlyObjectProperty<T> valueProperty() {
        return value.getReadOnlyProperty();
    }

    @Override
    public Throwable getException() {
        return exception.get();
    }

    @Override
    public ReadOnlyObjectProperty<Throwable> exceptionProperty() {
        return exception.getReadOnlyProperty();
    }

    @Override
    public double getWorkDone() {
        return workDone.get();
    }

    @Override
    public ReadOnlyDoubleProperty workDoneProperty() {
        return workDone.getReadOnlyProperty();
    }

    @Override
    public double getTotalWork() {
        return totalWork.get();
    }

    @Override
    public ReadOnlyDoubleProperty totalWorkProperty() {
        return totalWork.getReadOnlyProperty();
    }

    @Override
    public double getProgress() {
        return progress.get();
    }

    @Override
    public ReadOnlyDoubleProperty progressProperty() {
        return progress.getReadOnlyProperty();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public ReadOnlyBooleanProperty runningProperty() {
        return running.getReadOnlyProperty();
    }

    @Override
    public String getMessage() {
        return message.get();
    }

    @Override
    public ReadOnlyStringProperty messageProperty() {
        return message.getReadOnlyProperty();
    }

    @Override
    public String getTitle() {
        return title.get();
    }

    @Override
    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }
}

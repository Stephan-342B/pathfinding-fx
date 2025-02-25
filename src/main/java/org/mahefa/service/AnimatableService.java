package org.mahefa.service;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Duration;
import org.mahefa.common.enumerator.AnimationSpeed;
import org.mahefa.common.enumerator.ServiceState;

import java.util.concurrent.atomic.AtomicInteger;

public  class AnimatableService {

    private Timeline timeline;
    private AnimationTimer animationTimer;

    protected ObjectProperty<AnimationSpeed> currentSpeed = new SimpleObjectProperty<>();
    private ObjectProperty<ServiceState> currentState = new SimpleObjectProperty<>(ServiceState.IDLE);

    public AnimatableService() {
        currentState.addListener((observableValue, serviceState, t1) -> {
            switch (t1) {
                case COMPLETED:
                    completed();
                    break;
                case FAILED:
                case STOPPING:
                    cancelled();
                    break;
            }
        });
    }

    public synchronized void run(AnimationTimer currentAnimationTimer) {
        currentState.setValue(ServiceState.RUNNING);

        String[] dots = { "", ".", "..", "..." };
        AtomicInteger i = new AtomicInteger();

        // Perform the first print immediately
        System.out.print("\rRunning task " + dots[i.get() % dots.length]);

        timeline = new Timeline(
                new KeyFrame(Duration.millis(500), event -> {
                    System.out.print("\rRunning task " + dots[i.getAndIncrement() % dots.length]);
                })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        currentAnimationTimer.start();
        animationTimer = currentAnimationTimer;
    }

    public synchronized boolean isRunning() {
        return currentState.get().equals(ServiceState.RUNNING);
    }

    public ServiceState getCurrentState() {
        return currentState.get();
    }

    public ObjectProperty<ServiceState> currentStateProperty() {
        return currentState;
    }

    public void setCurrentState(ServiceState currentState) {
        this.currentState.set(currentState);
    }

    public void updateSpeed(AnimationSpeed speed) {
        this.currentSpeed.setValue(speed);
    }

    private synchronized void completed() {
        if (timeline != null)
            timeline.stop();

        currentState.setValue(ServiceState.IDLE);
        System.out.print("\rTask complete!");
    }

    private synchronized void cancelled() {
        if (isRunning()) {
            if (animationTimer != null)
                animationTimer.stop();

            if (timeline != null)
                timeline.stop();

            System.out.print("\rTask aborted!");
        }

        currentState.setValue(ServiceState.IDLE);
    }

    private void reset() {

    }
}

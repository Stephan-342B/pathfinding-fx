package com.mahefa.pathfindingfx.algorithm;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@Deprecated(forRemoval = true) // superseded by the Stepper/StepPlayer pipeline; kept until removal
public class State {

    protected BooleanProperty isRunning = new SimpleBooleanProperty();

    public boolean isRunning() {
        return isRunning.get();
    }

    public BooleanProperty isRunningProperty() {
        return isRunning;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }
}

package com.mahefa.pathfindingfx.domain.enumerator;

public enum Rotate {

    FORWARD(0d), RIGHT(90d), LEFT(-90d);

    private final double angle;

    Rotate(double angle) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }
}
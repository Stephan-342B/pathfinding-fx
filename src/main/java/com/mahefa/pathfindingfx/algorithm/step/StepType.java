package com.mahefa.pathfindingfx.algorithm.step;

/**
 * The kinds of visualization events an algorithm ({@link Stepper}) can emit. Shared by both maze
 * generators and pathfinders so a single renderer/player handles every algorithm.
 */
public enum StepType {
    /** The cell the algorithm is currently working on (transient highlight). */
    CURRENT,
    /** A cell finalized by a search (added to the closed set). */
    VISITED,
    /** A cell/passage opened (maze carve, or opening the start/target). */
    OPEN,
    /** A wall placed (e.g. the random maze). */
    WALL,
    /** A cell on the resulting shortest path (carries a rotation angle for the arrow). */
    PATH
}

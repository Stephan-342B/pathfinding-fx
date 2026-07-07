package com.mahefa.pathfindingfx.domain.enumerator;

public enum PathFindingAlgorithm {
    DIJKSTRA("Dijkstra"),
    A_STAR("A*"),
    BREADTH_FIRST_SEARCH("Breadth-First Search"),
    DEPTH_FIRST_SEARCH("Depth-First Search");

    private final String label;

    PathFindingAlgorithm(String label) {
        this.label = label;
    }

    /** Human-readable name for the console narrative / UI. */
    public String getLabel() {
        return label;
    }
}

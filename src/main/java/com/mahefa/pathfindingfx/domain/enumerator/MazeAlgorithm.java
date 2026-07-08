package com.mahefa.pathfindingfx.domain.enumerator;

public enum MazeAlgorithm {
    BASIC_RANDOM("Random"),
    ALDOUS_BRODER("Aldous-Broder"),
    PRIM("Prim"),
    RECURSIVE_DIVISION("Recursive Division"),
    RECURSIVE_DIVISION_VERT_SKEW("Recursive Division (vertical skew)"),
    RECURSIVE_DIVISION_HOZ_SKEW("Recursive Division (horizontal skew)");

    private final String label;

    MazeAlgorithm(String label) {
        this.label = label;
    }

    /** Human-readable name for the console narrative / UI. */
    public String getLabel() {
        return label;
    }
}

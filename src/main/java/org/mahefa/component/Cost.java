package org.mahefa.component;

public class Cost extends ComparableNode {

    // A* variables
    private int f = 0;
    private int g = 0;      // Cost of the path from the start node to n
    private int h = 0;      // Heuristic function that estimates the cost of the cheapest path from n to the goal

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}

package com.mahefa.pathfindingfx.ui.style;

import javafx.css.PseudoClass;

public class CellStyle {

    public static final PseudoClass WALL_NODE_PSEUDO_CLASS = PseudoClass.getPseudoClass("wall");
    public static final PseudoClass SHORTEST_PATH_NODE_PSEUDO_CLASS = PseudoClass.getPseudoClass("shortest-path");
    public static final PseudoClass CURRENT_PSEUDO_CLASS = PseudoClass.getPseudoClass("current");
    public static final PseudoClass VISITED_PSEUDO_CLASS = PseudoClass.getPseudoClass("visited");

    public enum Flag {
        NONE, WALL_NODE, SHORTEST_PATH_NODE, CURRENT, VISITED;
    }
}
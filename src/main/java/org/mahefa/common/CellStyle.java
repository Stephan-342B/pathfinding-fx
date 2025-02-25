package org.mahefa.common;

import javafx.css.PseudoClass;

public class CellStyle {

    public static final PseudoClass WALL_NODE_PSEUDO_CLASS = PseudoClass.getPseudoClass("wall");
    public static final PseudoClass PATH_NODE_PSEUDO_CLASS = PseudoClass.getPseudoClass("path");
    public static final PseudoClass POINTER_PSEUDO_CLASS = PseudoClass.getPseudoClass("pointer");
    public static final PseudoClass VISITED_PSEUDO_CLASS = PseudoClass.getPseudoClass("visited");

    public enum Flag {
        NONE, WALL_NODE, PATH_NODE, POINTER, VISITED;
    }
}
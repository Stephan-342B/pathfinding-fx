package org.mahefa.common;

import javafx.css.PseudoClass;

public class CellStyle {

    public static final PseudoClass START_PSEUDO_CLASS = PseudoClass.getPseudoClass("start");
    public static final PseudoClass TARGET_PSEUDO_CLASS = PseudoClass.getPseudoClass("target");
    public static final PseudoClass WALL_PSEUDO_CLASS = PseudoClass.getPseudoClass("wall");
    public static final PseudoClass PATH_PSEUDO_CLASS = PseudoClass.getPseudoClass("path");
    public static final PseudoClass POINTER_PSEUDO_CLASS = PseudoClass.getPseudoClass("pointer");
    public static final PseudoClass VISITED_PSEUDO_CLASS = PseudoClass.getPseudoClass("visited");
    public static final PseudoClass UNVISITED_PSEUDO_CLASS = PseudoClass.getPseudoClass("unvisited");

    public enum Flag {
        NONE, START, TARGET, WALL, PATH, POINTER, VISITED, UNVISITED;
    }
}
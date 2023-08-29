package org.mahefa.common;

import javafx.css.PseudoClass;

public class MenuStyle {

    public static final PseudoClass HOVER_PSEUDO_CLASS = PseudoClass.getPseudoClass("hover");
    public static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
    public static final PseudoClass ACTIVE_DROPDOWN_PSEUDO_CLASS = PseudoClass.getPseudoClass("active-drop-down");

    public enum State {
        NONE, HOVER, ACTIVE, ACTIVE_WITH_DROPDOWN;
    }

//    public static class SubmenuStyle {
//
//        public static final PseudoClass ACTIVE_PSEUDO_CLASS = PseudoClass.getPseudoClass("active");
//
//        public enum State {
//            NONE, ACTIVE;
//        }
//    }
}




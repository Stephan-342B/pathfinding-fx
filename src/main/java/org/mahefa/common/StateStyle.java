package org.mahefa.common;

import javafx.css.PseudoClass;

public class StateStyle {

    public static final PseudoClass BLOCKED_PSEUDO_CLASS = PseudoClass.getPseudoClass("blocked");
    public static final PseudoClass DISABLED_PSEUDO_CLASS = PseudoClass.getPseudoClass("disabled");

    public enum State {
        READY, BLOCKED, DISABLED
    }
}
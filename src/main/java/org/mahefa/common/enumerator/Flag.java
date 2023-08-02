package org.mahefa.common.enumerator;

public enum Flag {
    START("start"),
    TARGET("target"),
    WALL("wall"),
    PATH("path"),
    POINTER("pointer"),
    VISITED("visited"),
    UNVISITED("unvisited");

    private final String css;

    Flag(String css) {
        this.css = css;
    }

    public String getCssClassValue() {
        return css;
    }

    public static Flag valueFor(String cssClass) {
        for (Flag flag : Flag.values()) {
            if (flag.css.equals(cssClass)) {
                return flag;
            }
        }

        throw new IllegalArgumentException("No enum constant with css: " + cssClass);
    }
}

package com.mahefa.pathfindingfx.ui.animation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Native ({@code javafx.animation}) cell/node animations — no third-party dependency. JavaFX CSS has
 * no {@code @keyframes}, so the multi-step "pulse" reveals (scale + colour morph) from the original
 * web CSS are reproduced here with {@link Timeline} keyframes. Simple colour states (wall/current)
 * are left to CSS transitions in {@code cell.scss}.
 */
public final class NodeAnimations {

    private NodeAnimations() {
    }

    /** Quick pop-in: scales a node up from 0.3 to normal size (ease-out). Used for start/target icons. */
    public static void pop(Node node) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(250), node);
        scale.setFromX(0.3);
        scale.setFromY(0.3);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);
        scale.play();
    }

    /**
     * Visited-cell reveal (port of the web {@code visitedAnimation}): a scale pulse plus a background
     * morph dark-blue → blue → green → cyan, ending filled cyan. 1.5s, ease-out.
     */
    public static void visited(Region region) {
        new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(region.scaleXProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(region.scaleYProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(region.backgroundProperty(), fill(Color.rgb(0, 0, 66, 0.75), 100), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(0.75),
                        new KeyValue(region.backgroundProperty(), fill(Color.rgb(17, 104, 217, 0.75), 0), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1.125),
                        new KeyValue(region.scaleXProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(region.scaleYProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(region.backgroundProperty(), fill(Color.rgb(0, 217, 159, 0.75), 0), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(region.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(region.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(region.backgroundProperty(), fill(Color.rgb(0, 190, 218, 0.75), 0), Interpolator.EASE_OUT))
        ).play();
    }

    /**
     * Shortest-path reveal (port of the web {@code triangletwo}): a scale pulse 0.6 → 1.2 → 1.0.
     * The yellow fill/border come from CSS ({@code .cell:shortest-path}). 1.5s, linear.
     */
    public static void shortestPath(Region region) {
        new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(region.scaleXProperty(), 0.6, Interpolator.LINEAR),
                        new KeyValue(region.scaleYProperty(), 0.6, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(0.75),
                        new KeyValue(region.scaleXProperty(), 1.2, Interpolator.LINEAR),
                        new KeyValue(region.scaleYProperty(), 1.2, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(region.scaleXProperty(), 1.0, Interpolator.LINEAR),
                        new KeyValue(region.scaleYProperty(), 1.0, Interpolator.LINEAR))
        ).play();
    }

    private static Background fill(Color color, double radius) {
        return new Background(new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY));
    }
}

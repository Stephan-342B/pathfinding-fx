package com.mahefa.pathfindingfx.ui.animation;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Native ({@code javafx.animation}) cell/node animations — no third-party dependency. JavaFX CSS has
 * no {@code @keyframes}, so the multi-step "pulse" reveals (scale + colour morph) from the original
 * web CSS are reproduced here with {@link Timeline} keyframes. Simple colour states (wall/current)
 * are left to CSS transitions in {@code cell.scss}.
 *
 * <p>The scale/colour pulse runs on a <b>transient overlay tile</b> added to the cell for the duration
 * of the reveal and removed when it ends. The cell itself is never scaled, so its top/left border —
 * which doubles as the shared gridline for its neighbours — stays put instead of collapsing when the
 * pulse shrinks a cell. The tile is created on demand and discarded, so there is no permanent per-cell
 * node cost. The cell's persistent colour is applied once the reveal finishes (visited) or via CSS
 * (shortest-path).
 *
 * <p>At fast speeds a cell's flag can change (e.g. {@code VISITED → SHORTEST_PATH}) before its 1.5s
 * reveal finishes. Each cell keeps a reference to its running reveal; starting a new one (or resetting
 * the cell) stops the old animation and removes its tile so the two never fight or leak.
 */
public final class NodeAnimations {

    /** Key under which a cell's currently running {@link Reveal} is stashed in its properties map. */
    private static final String RUNNING_KEY = "pfx.runningReveal";

    private static final Color VISITED_END = Color.rgb(0, 190, 218, 0.75);   // -visited-color
    private static final Color PATH_COLOR = Color.rgb(255, 254, 106);        // -shortest-path-color

    private NodeAnimations() {
    }

    /** A running reveal: the animation plus the transient overlay tile to remove when it ends (may be null). */
    private record Reveal(Animation animation, Node tile) {
    }

    /** Wall pop-in: a scale bounce 0.3 → 1.2 → 1.0 (ease-out), 0.3s — as in the original. */
    public static void pop(Node node) {
        popScale(node, Duration.seconds(0.3));
    }

    /** Start/target icon pop-in: the same 0.3 → 1.2 → 1.0 bounce but slow (2s), as in the original. */
    public static void popIcon(Node node) {
        popScale(node, Duration.seconds(2));
    }

    /** Shortest-path arrow bounce 0.6 → 1.2 → 1.0 (linear) as it lands on a path cell — the original pop, on the arrow. */
    public static void pathPop(Node node) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), 0.6, Interpolator.LINEAR),
                        new KeyValue(node.scaleYProperty(), 0.6, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(0.2),
                        new KeyValue(node.scaleXProperty(), 1.2, Interpolator.LINEAR),
                        new KeyValue(node.scaleYProperty(), 1.2, Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(0.4),
                        new KeyValue(node.scaleXProperty(), 1.0, Interpolator.LINEAR),
                        new KeyValue(node.scaleYProperty(), 1.0, Interpolator.LINEAR))
        );
        play(node, null, timeline);
    }

    /** Scale bounce 0.3 → 1.2 → 1.0 (ease-out) over {@code duration}. */
    private static void popScale(Node node, Duration duration) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(node.scaleXProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 0.3, Interpolator.EASE_OUT)),
                new KeyFrame(duration.divide(2),
                        new KeyValue(node.scaleXProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 1.2, Interpolator.EASE_OUT)),
                new KeyFrame(duration,
                        new KeyValue(node.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(node.scaleYProperty(), 1.0, Interpolator.EASE_OUT))
        );
        play(node, null, timeline);
    }

    /** Style class that keeps a :visited cell transparent while its reveal tile plays (see cell.scss). */
    private static final String REVEALING_CLASS = "revealing";

    /**
     * Visited-cell reveal (port of the web {@code visitedAnimation}): a scale pulse plus a background
     * morph dark-blue → blue → green → cyan. Runs on a transient tile. The cell keeps the "revealing"
     * class for the duration so it stays transparent (the tile does the fill, growing "from nothing");
     * on finish the class is dropped and the CSS {@code :visited} cyan takes over. 1.5s, ease-out.
     */
    public static void visited(Pane cell) {
        if (!cell.getStyleClass().contains(REVEALING_CLASS)) {
            cell.getStyleClass().add(REVEALING_CLASS);
        }
        Region tile = overlayTile(cell);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(tile.scaleXProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(tile.scaleYProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(tile.backgroundProperty(), fill(Color.rgb(0, 0, 66, 0.75), 100), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(0.75),
                        new KeyValue(tile.backgroundProperty(), fill(Color.rgb(17, 104, 217, 0.75), 0), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1.125),
                        new KeyValue(tile.scaleXProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(tile.scaleYProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(tile.backgroundProperty(), fill(Color.rgb(0, 217, 159, 0.75), 0), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(tile.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(tile.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(tile.backgroundProperty(), fill(VISITED_END, 0), Interpolator.EASE_OUT))
        );
        // Reveal done: drop "revealing" so the CSS :visited cyan shows (the tile is removed by play()).
        timeline.setOnFinished(e -> cell.getStyleClass().remove(REVEALING_CLASS));
        play(cell, tile, timeline);
    }

    /**
     * Shortest-path endpoint reveal, played when a dragged start/target is dropped: a tile grows from a
     * rounded square to a full yellow square (scale pulse + corner morph). The cell's persistent yellow
     * comes from CSS {@code :shortest-path}; the tile is removed on finish.
     */
    public static void pathReveal(Pane cell) {
        Region tile = overlayTile(cell);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(tile.scaleXProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(tile.scaleYProperty(), 0.3, Interpolator.EASE_OUT),
                        new KeyValue(tile.backgroundProperty(), fill(PATH_COLOR, 100), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(0.75),
                        new KeyValue(tile.scaleXProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(tile.scaleYProperty(), 1.2, Interpolator.EASE_OUT),
                        new KeyValue(tile.backgroundProperty(), fill(PATH_COLOR, 0), Interpolator.EASE_OUT)),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(tile.scaleXProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(tile.scaleYProperty(), 1.0, Interpolator.EASE_OUT),
                        new KeyValue(tile.backgroundProperty(), fill(PATH_COLOR, 0), Interpolator.EASE_OUT))
        );
        play(cell, tile, timeline);
    }

    /**
     * Visited end-state with no reveal: the cyan comes from the CSS {@code :visited} rule, so we just
     * stop any running reveal and clear leftover transforms. Used for the instant recompute while
     * dragging start/target, where the 1.5s pulse would be unusable.
     */
    public static void visitedInstant(Region cell) {
        stopRunning(cell);
        cell.getStyleClass().remove(REVEALING_CLASS);
        cell.setScaleX(1);
        cell.setScaleY(1);
        cell.setBackground(null);   // let the CSS :visited cyan show through
    }

    /**
     * Shortest-path cell: colours to yellow via CSS ({@code .cell:shortest-path}). The pop lives on the
     * arrow ({@link #pathPop}), not the cell, so the cell — and its gridline border — never scales.
     * This just stops any running visited reveal and drops its inline fill/scale so the CSS shows through.
     */
    public static void shortestPath(Pane cell) {
        stopRunning(cell);
        cell.getStyleClass().remove(REVEALING_CLASS);
        cell.setBackground(null);
        cell.setBorder(null);
        cell.setScaleX(1);
        cell.setScaleY(1);
    }

    /**
     * Stops any reveal still running on {@code cell} (removing its transient tile) and releases the
     * programmatically applied background/border so CSS takes over again. Used when a cell returns to a
     * plain flag (NONE/CURRENT).
     */
    public static void reset(Pane cell) {
        stopRunning(cell);
        cell.getStyleClass().remove(REVEALING_CLASS);
        cell.setBackground(null);
        cell.setBorder(null);
        cell.setScaleX(1);
        cell.setScaleY(1);
    }

    /** A transient overlay sized to the cell, sitting on top of it for the duration of a reveal. */
    private static Region overlayTile(Pane cell) {
        Region tile = new Region();
        tile.setManaged(false);          // we position it ourselves; keep it out of the cell's layout
        tile.setMouseTransparent(true);
        double w = cell.getWidth() > 0 ? cell.getWidth() : cell.getPrefWidth();
        double h = cell.getHeight() > 0 ? cell.getHeight() : cell.getPrefHeight();
        tile.resizeRelocate(0, 0, w, h);
        return tile;
    }

    /**
     * Stops any reveal already running on {@code host}, then plays the new one. {@code tile} (if any) is
     * added to {@code host} for the reveal and removed when it finishes; on interruption {@link #stopRunning}
     * removes it instead.
     */
    private static void play(Node host, Node tile, Animation animation) {
        stopRunning(host);
        if (tile != null && host instanceof Pane pane) {
            pane.getChildren().add(tile);
        }
        Reveal reveal = new Reveal(animation, tile);
        host.getProperties().put(RUNNING_KEY, reveal);
        // Chain onto any onFinished the caller set (e.g. visited() persisting its cyan) rather than replace it.
        EventHandler<ActionEvent> callerOnFinished = animation.getOnFinished();
        animation.setOnFinished(e -> {
            host.getProperties().remove(RUNNING_KEY, reveal);
            removeTile(host, tile);
            if (callerOnFinished != null) {
                callerOnFinished.handle(e);
            }
        });
        animation.play();
    }

    private static void stopRunning(Node host) {
        Object running = host.getProperties().remove(RUNNING_KEY);
        if (running instanceof Reveal reveal) {
            reveal.animation().stop();
            removeTile(host, reveal.tile());
        }
    }

    private static void removeTile(Node host, Node tile) {
        if (tile != null && host instanceof Pane pane) {
            pane.getChildren().remove(tile);
        }
    }

    private static Background fill(Color color, double radius) {
        return new Background(new BackgroundFill(color, new CornerRadii(radius), Insets.EMPTY));
    }
}

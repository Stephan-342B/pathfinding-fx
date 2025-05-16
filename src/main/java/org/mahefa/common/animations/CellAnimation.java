package org.mahefa.common.animations;

import de.schlegel11.jfxanimation.JFXAnimationTemplate;
import de.schlegel11.jfxanimation.JFXTemplateBuilder;
import javafx.animation.Interpolator;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.mahefa.component.Cell;

public final class CellAnimation {

    public static final JFXTemplateBuilder<Cell> WALL_ANIMATION = JFXAnimationTemplate.create(Cell.class)
            .percent(0)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(.3))
            .percent(50)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(1.2))
            .percent(100)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(1.0))
            .config(b -> b.duration(Duration.seconds(0.3)).autoReverse(true).cycleCount(1).interpolator(Interpolator.EASE_OUT));

    public static final JFXTemplateBuilder<Cell> VISITED_ANIMATION = JFXAnimationTemplate.create(Cell.class)
            .percent(0)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(0.3))
            .action(b -> b.target(Cell::backgroundProperty).endValue(new Background(new BackgroundFill(Color.rgb(0, 0, 66, 0.75),  new CornerRadii(100), null))))
            .percent(50)
            .action(b -> b.target(Cell::backgroundProperty).endValue(new Background(new BackgroundFill(Color.rgb(17, 104, 217, 0.75), CornerRadii.EMPTY, null))))
            .percent(75)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(1.2))
            .action(b -> b.target(Cell::backgroundProperty).endValue(new Background(new BackgroundFill(Color.rgb(0, 217, 159, 0.75), CornerRadii.EMPTY, null))))
            .percent(100)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(1.0))
            .action(b -> b.target(Cell::backgroundProperty).endValue(new Background(new BackgroundFill(Color.rgb(0, 190, 218, 0.75), CornerRadii.EMPTY, null))))
            .config(b -> b.duration(Duration.seconds(1.5)).autoReverse(true).cycleCount(1).interpolator(Interpolator.EASE_OUT));

    public static final JFXTemplateBuilder<Cell> SHORTEST_PATH_ANIMATION = JFXAnimationTemplate.create(Cell.class)
            .percent(0)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(0.6))
            .percent(50)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(1.2))
            .percent(100)
            .action(b -> b.target(Cell::scaleXProperty, Cell::scaleYProperty).endValue(1.0))
            .config(b -> b.duration(Duration.seconds(1.5)).autoReverse(true).cycleCount(1).interpolator(Interpolator.LINEAR));

    public static final JFXTemplateBuilder<Node> SPECIAL_NODES_ANIMATION = JFXAnimationTemplate.create(Node.class)
            .percent(0)
            .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(0.3))
            .percent(50)
            .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.2))
            .percent(100)
            .action(b -> b.target(Node::scaleXProperty, Node::scaleYProperty).endValue(1.0))
            .config(b -> b.duration(Duration.seconds(2)).autoReverse(true).cycleCount(1).interpolator(Interpolator.EASE_OUT));
}
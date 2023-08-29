package org.mahefa.common.custom_animations;

import javafx.animation.Transition;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.mahefa.component.Menu;

public class ColorTransition extends Transition {

    private final Color startColor;
    private final Color endColor;
    private final Menu target;

    public ColorTransition(Duration duration, Color newBgColor, Menu target) {
        this.setCycleDuration(duration);
        this.endColor = newBgColor;
        this.target = target;

        Background background = target.getBackground();

        if (background != null && !background.getFills().isEmpty()) {
            this.startColor = (Color) background.getFills().get(0).getFill();
        } else {
            this.startColor = Color.TRANSPARENT;
        }
    }

    @Override
    protected void interpolate(double frac) {
        Color interpolatedColor = interpolateColor(startColor, endColor, frac);
        BackgroundFill backgroundFill = new BackgroundFill(interpolatedColor, null, null);
        Background background = new Background(backgroundFill);
        target.setBackground(background);
    }

    private Color interpolateColor(Color start, Color end, double fraction) {
        return start.interpolate(end, fraction);
    }
}

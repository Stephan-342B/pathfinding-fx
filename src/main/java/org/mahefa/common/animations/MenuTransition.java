package org.mahefa.common.animations;

import javafx.animation.Transition;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.mahefa.component.Menu;

public class MenuTransition extends Transition {

    public enum Style {
        FADE_IN, FADE_OUT
    }

    private Color bgStartColor;
    private Color bgEndColor;
    private Color startColor;
    private Color endColor;
    private Menu target;

    public MenuTransition(Duration duration, Menu currentMenuItem, Style style) {
        if (currentMenuItem != null) {
            Background background = currentMenuItem.getBackground();
            this.bgStartColor = (background != null && !background.getFills().isEmpty())
                    ? (Color) background.getFills().get(0).getFill()
                    : Color.TRANSPARENT;
            this.bgEndColor = (currentMenuItem.hasItems() && this.bgStartColor.equals(Color.TRANSPARENT)) ? Color.valueOf("#1ABC9C") : Color.TRANSPARENT;
            this.startColor = (style.equals(Style.FADE_IN)) ? Color.WHITE : Color.valueOf("#1ABC9C");
            this.endColor = (style.equals(Style.FADE_IN)) ? Color.valueOf("#1ABC9C") : Color.WHITE;
            this.target = currentMenuItem;

            final String cssClass = (currentMenuItem.hasItems() && this.bgStartColor.equals(Color.TRANSPARENT)) ? "active-drop-down" : "active";

            this.setCycleDuration(duration);
            this.setOnFinished(actionEvent -> {
                if (style.equals(Style.FADE_OUT)) {
                    this.target.getStyleClass().removeAll("active-drop-down", "active");
                    return;
                }

                this.target.getStyleClass().add(cssClass);
            });
        }
    }

    @Override
    protected void interpolate(double frac) {
        if (target.hasItems()) {
            Color interpolatedColor = interpolateColor(bgStartColor, bgEndColor, frac);
            BackgroundFill initialFill = (target.getBackground() != null)
                    ? target.getBackground().getFills().get(0)
                    : new BackgroundFill(Color.TRANSPARENT, null, null);
            BackgroundFill backgroundFill = new BackgroundFill(interpolatedColor, initialFill.getRadii(), initialFill.getInsets());
            Background background = new Background(backgroundFill);
            target.setBackground(background);
        }

        // Interpolate text color
        Color interpolatedTextColor = interpolateColor(startColor, endColor, frac);
        target.getLabel().setFill(interpolatedTextColor);

        // Interpolate icon color
        Color iconEndColor = (!endColor.equals(Color.WHITE)) ? endColor : Color.valueOf("#4B6075");
        Color interpolatedIconColor = interpolateColor(startColor, iconEndColor, frac);
        target.getFontIcon().setIconColor(interpolatedIconColor);
    }

    private Color interpolateColor(Color start, Color end, double fraction) {
        return start.interpolate(end, fraction);
    }
}

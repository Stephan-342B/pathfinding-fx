package org.mahefa.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.mahefa.common.custom_animations.BackgroundColorTransition;
import org.mahefa.events.MenuEventHandler;

import java.io.IOException;

import static org.mahefa.common.MenuStyle.*;

public class Menu extends HBox {

    @FXML
    private Text label;

    @FXML
    private FontIcon fontIcon;

    private Submenu submenu;

    private MenuEventHandler menuEventHandler = new MenuEventHandler();

    private ObjectProperty<State> state = new SimpleObjectProperty<>() {
        @Override
        public void invalidated() {
            pseudoClassStateChanged(HOVER_PSEUDO_CLASS, false);
            pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, false);
            pseudoClassStateChanged(ACTIVE_DROPDOWN_PSEUDO_CLASS, false);

            switch (get()) {
                case HOVER:
                    pseudoClassStateChanged(HOVER_PSEUDO_CLASS, true);
                    break;
                case ACTIVE:
                    pseudoClassStateChanged(ACTIVE_PSEUDO_CLASS, true);
                    break;
                case ACTIVE_WITH_DROPDOWN:
                    pseudoClassStateChanged(ACTIVE_DROPDOWN_PSEUDO_CLASS, true);
                    break;
                default:
                    break;
            }
        }
    };

    public Menu() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/Menu.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        // set FXMLLoader's classloader!
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try {
            fxmlLoader.load();

            sceneProperty().addListener((observable, oldScene, newScene) -> {
                if (newScene != null && this.submenu == null && Submenu.isPresent(getId())) {
                    this.submenu = new Submenu(this);
                }
            });

            state.addListener((observableValue, state1, t1) -> {
                Color newBgColor = Color.TRANSPARENT;

                if (t1 != null && t1.equals(State.ACTIVE_WITH_DROPDOWN))
                    newBgColor = Color.valueOf("#1ABC9C");

                new BackgroundColorTransition(Duration.millis(250), newBgColor, this).play();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Submenu getSubmenu() {
        return submenu;
    }

    public State getState() {
        return state.get();
    }

    public ObjectProperty<State> stateProperty() {
        return state;
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public MenuEventHandler getMenuEventHandler() {
        return menuEventHandler;
    }

    public boolean hasSubmenu() {
        return this.submenu != null;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String value) {
        textProperty().set(value);
    }

    public StringProperty textProperty() {
        return label.textProperty();
    }

    public String getIconLiteral() {
        return fontIcon.getIconLiteral();
    }

    public void setIconLiteral(String value) {
        fontIcon.setIconLiteral(value);
    }

    public Integer getIconSize() {
        return iconSizeProperty().get();
    }

    public void setIconSize(Integer size) {
        iconSizeProperty().set(size);
    }

    public IntegerProperty iconSizeProperty() {
        return fontIcon.iconSizeProperty();
    }
}

package org.mahefa.component;

import javafx.animation.ParallelTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.mahefa.common.animations.MenuTransition;
import org.mahefa.events.MenuEventHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Navbar extends FlowPane implements Initializable {

    @FXML private HBox title;
    @FXML private Text label;
    @FXML private MenuBar menuBar;

    private StackPane area;

    private ObjectProperty<Menu> currentActiveMenu = new SimpleObjectProperty<>(null);
    private ObjectProperty<MenuItem> currentActiveMenuItem = new SimpleObjectProperty<>(null);

    private MenuEventHandler menuEventHandler = new MenuEventHandler();

    public Navbar() {
        super();

        // Load the FXML file for the custom Navbar component
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/component/Navbar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        // set FXMLLoader's classloader!
        fxmlLoader.setClassLoader(getClass().getClassLoader());

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuBar.prefWidthProperty().bind(
                Bindings.when(prefWidthProperty().subtract(title.widthProperty()).subtract(42).greaterThanOrEqualTo(menuBar.widthProperty()))
                .then(prefWidthProperty().subtract(title.widthProperty()).subtract(42))
                .otherwise(prefWidthProperty().subtract(42))
        );

        menuEventHandler.attachObservableProperty(currentActiveMenu);
        menuBar.getMenus().forEach(menu ->
                menu.onMouseClickedProperty().set(event -> {
                    currentActiveMenu.set((Menu) event.getSource());
                    getMenuEventHandler().handle(event);
                })
        );

        currentActiveMenu.addListener((observableValue, old, current) -> {
            ParallelTransition parallelTransition = new ParallelTransition();

            if(old != null) {
                old.getStyleClass().removeAll("active-drop-down", "active");

                if (old.hasItems()) {
                    area.getChildren().remove(1);
                }

                parallelTransition.getChildren().add(new MenuTransition(Duration.millis(250), old, MenuTransition.Style.FADE_OUT));
            }

            if (current != null) {
                if (current.hasItems()) {
                    final Submenu submenu = new Submenu(current);
                    area.getChildren().add(1, submenu);
                    area.setAlignment(submenu, Pos.TOP_LEFT);
                }

                parallelTransition.getChildren().add(new MenuTransition(Duration.millis(250), current, MenuTransition.Style.FADE_IN));
            }

            parallelTransition.play();
        });
    }

    public StackPane getArea() {
        return this.area;
    }

    public void setArea(StackPane area) {
        this.area = area;
    }

    public String getTitle() {
        return titleProperty().get();
    }

    public void setTitle(String value) {
        titleProperty().set(value);
    }

    public StringProperty titleProperty() {
        return label.textProperty();
    }

    public Menu getCurrentActiveMenu() {
        return currentActiveMenu.get();
    }

    public void setCurrentActiveMenu(Menu currentActiveMenu) {
        this.currentActiveMenu.set(currentActiveMenu);
    }

    public ObjectProperty<Menu> currentActiveMenuProperty() {
        return currentActiveMenu;
    }

    public MenuItem getCurrentActiveMenuItem() {
        return currentActiveMenuItem.get();
    }

    public ObjectProperty<MenuItem> currentActiveMenuItemProperty() {
        return currentActiveMenuItem;
    }

    public void setCurrentActiveMenuItem(MenuItem currentActiveMenuItem) {
        this.currentActiveMenuItem.set(currentActiveMenuItem);
    }

    public MenuEventHandler getMenuEventHandler() {
        return menuEventHandler;
    }
}

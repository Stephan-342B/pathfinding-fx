package org.mahefa.component;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import org.mahefa.events.MenuItemEventHandler;

public class Submenu extends Pane {

    private final Menu menu;

    private MenuItemEventHandler menuItemEventHandler = new MenuItemEventHandler();

    public Submenu(Menu menu) {
        super();

        this.menu = menu;
        initialize();
    }

    public void initialize() {
        setPickOnBounds(false);

        FlowPane flowPane = new FlowPane();
        flowPane.setId("submenu");
        flowPane.setOrientation(Orientation.HORIZONTAL);

        menu.getItems().stream().forEach(menuItem -> {
            menuItem.getStyleClass().add("menuItem");
            menuItem.setMenu(menu);

            flowPane.getChildren().add(menuItem);

            menuItem.onMouseClickedProperty().set(event -> {
                getMenuItemEventHandler().handle(event);
            });
        });

        getChildren().add(flowPane);

        flowPane.prefWidthProperty().bind(menu.widthProperty());
        flowPane.prefWrapLengthProperty().bind(menu.widthProperty());

        // Bindings for submenu position
        MenuBar menuBar = (MenuBar) menu.getParent();
        Navbar navbar = (Navbar) menuBar.getParent();

        translateXProperty().bind(
                Bindings.createDoubleBinding(() -> menu.getLayoutX() + menuBar.getLayoutX(),
                        menu.layoutXProperty(), menuBar.layoutXProperty())
        );

        translateYProperty().bind(
                Bindings.createDoubleBinding(() ->
                                menu.getLayoutY() + menu.getPrefHeight() + (navbar.getHeight() - menuBar.getHeight()) + 9,
                        menu.layoutYProperty(), menu.prefHeightProperty(),  navbar.heightProperty(), menuBar.heightProperty())
        );
    }

    public Menu getMenu() {
        return menu;
    }

    public MenuItemEventHandler getMenuItemEventHandler() {
        return menuItemEventHandler;
    }
}

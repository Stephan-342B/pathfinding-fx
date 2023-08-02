package org.mahefa.data.builder;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class Navbar {

    private final FlowPane navbarPane;
    private final StackPane stackPane;
    private final SimpleObjectProperty<HBox> currentActiveMenu;

    private Navbar(Builder builder) {
        this.navbarPane = builder.navbarPane;
        this.stackPane = builder.stackPane;
        this.currentActiveMenu = builder.currentActiveMenu;
    }

    public FlowPane getNavbarPane() {
        return navbarPane;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public HBox getCurrentActiveMenu() {
        return currentActiveMenu.get();
    }

    public SimpleObjectProperty<HBox> currentActiveMenuProperty() {
        return currentActiveMenu;
    }

    public void setMenuToDefault() {
        currentActiveMenu.setValue(null);
    }

    public static class Builder {

        private final FlowPane navbarPane;
        private StackPane stackPane;
        private SimpleObjectProperty<HBox> currentActiveMenu = new SimpleObjectProperty<>(null);

        public Builder(FlowPane navbarPane) {
            this.navbarPane = navbarPane;

            currentActiveMenu.addListener((observableValue, old, current) -> {
                if(old != null) {
                    old.getStyleClass().remove("active");

                    if (old.getUserData() != null)
                        stackPane.getChildren().remove(1);
                }

                if (current != null) {
                    // Set actual menu as current
                    current.getStyleClass().add("active");

                    Node submenu = (Node) current.getUserData();

                    if (submenu != null) {
                        stackPane.getChildren().add(1, submenu);
                        StackPane.setAlignment(submenu, Pos.TOP_LEFT);
                    }
                }
            });
        }

        public Builder setArea(StackPane stackPane) {
            this.stackPane = stackPane;
            return this;
        }

        public Navbar build() {
            navbarPane.getChildren().stream()
                    .filter(node -> node.getId() != null && node.getId().startsWith("menu_"))
                    .forEach(menu -> {
                        final String currentMenuId = menu.getId();
                        new MenuBuilder((HBox) menu).build();

                        menu.onMouseClickedProperty().set(mouseEvent -> {
                            Node currentActive = currentActiveMenu.get();
                            currentActiveMenu.setValue(
                                    (currentActive == null || !currentActive.getId().equals(currentMenuId)) ? (HBox) menu : null
                            );

                            // TODO: Action
                        });
                    });

            return new Navbar(this);
        }
    }
}

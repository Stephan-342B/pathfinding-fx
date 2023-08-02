package org.mahefa.data.builder;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MenuBuilder {

    private Map<String, List<String>> submenus = Map.ofEntries(
            Map.entry("menu_algorithms", Arrays.asList("Dijkstra's Algorithm", "A* Search", "Breadth-first Search", "Depth-first Search")),
            Map.entry("menu_mazes_patterns", Arrays.asList("Recursive Division", "Recursive Division (vertical skew)",
                    "Recursive Division (horizontal skew)", "Basic Random Maze", "Aldous Broder")),
            Map.entry("menu_speed", Arrays.asList("Fast", "Average", "Slow"))
    );

    private final HBox menu;

    public MenuBuilder(HBox menu) {
        this.menu = menu;
    }

    public void build() {
        Pane submenu = buildSubmenu();

        if (submenu != null)
            menu.setUserData(submenu);

//        menu.backgroundProperty().addListener((observableValue, old, newBck) -> {
//            if (newBck != null) {
//                // Get the initial and target background colors
//                Color initialColor = Color.TRANSPARENT;
//                Color targetColor = (Color) newBck.getFills().get(0).getFill();
//            }
//        });
    }

    private Pane buildSubmenu() {
        List<String> currentSubmenus = submenus.getOrDefault(menu.getId(), new ArrayList<>());

        if (CollectionUtils.isEmpty(currentSubmenus) || menu.getUserData() != null)
            return null;

        Pane submenu = new Pane();
        submenu.setPickOnBounds(false);

        FlowPane flowPane = new FlowPane();
        flowPane.setId("submenu");
        flowPane.setOrientation(Orientation.HORIZONTAL);
        flowPane.setPrefWrapLength(menu.getWidth());
        flowPane.setMinWidth(140.52);
        flowPane.setMaxWidth(263);

        currentSubmenus.stream().forEach(submenuStr -> {
            Text text = new Text(submenuStr);

            // Create a StackPane to hold the Text node
            StackPane stackPane = new StackPane(text);
            stackPane.setId("container");

            flowPane.getChildren().add(stackPane);

            StackPane.setMargin(text, new javafx.geometry.Insets(6, 9, 6, 9));
        });

        submenu.getChildren().add(flowPane);

        // Bindings for submenu position
        submenu.translateXProperty().bind(
                Bindings.createDoubleBinding(() -> menu.getLayoutX(), menu.layoutXProperty())
        );

        submenu.translateYProperty().bind(
                Bindings.createDoubleBinding(() ->
                                menu.getLayoutY() + ((HBox) menu).getPrefHeight() + 9,
                        menu.layoutYProperty(), ((HBox) menu).prefHeightProperty())
        );

        return submenu;
    }
}

package org.mahefa.component;

import javafx.beans.binding.Bindings;
import javafx.geometry.Orientation;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Submenu extends Pane {

    private static final Map<String, List<String>> submenus = Map.ofEntries(
            Map.entry("menu_algorithms", Arrays.asList("Dijkstra's Algorithm", "A* Search", "Breadth-first Search", "Depth-first Search")),
            Map.entry("menu_mazes_patterns", Arrays.asList("Recursive Division", "Recursive Division (vertical skew)",
                    "Recursive Division (horizontal skew)", "Basic Random Maze", "Aldous Broder")),
            Map.entry("menu_speed", Arrays.asList("Fast", "Average", "Slow"))
    );

    public Submenu(Menu menu) {
        super();

        final List<String> currentSubmenus = submenus.getOrDefault(menu.getId(), new ArrayList<>());

        if (!CollectionUtils.isEmpty(currentSubmenus)) {
            setPickOnBounds(false);

            FlowPane flowPane = new FlowPane();
            flowPane.setId("submenu");
            flowPane.setOrientation(Orientation.HORIZONTAL);
            flowPane.setPrefWrapLength(menu.getPrefWidth());
            flowPane.setMinWidth(128.219);

            currentSubmenus.stream().forEach(submenuStr -> {
                Text text = new Text(submenuStr);

                // Create a StackPane to hold the Text node
                StackPane stackPane = new StackPane(text);
                stackPane.setId("container");

                flowPane.getChildren().add(stackPane);

                StackPane.setMargin(text, new javafx.geometry.Insets(6, 9, 6, 9));
            });

            getChildren().add(flowPane);

            // Bindings for submenu position
            translateXProperty().bind(
                    Bindings.createDoubleBinding(() -> menu.getLayoutX(), menu.layoutXProperty())
            );

             translateYProperty().bind(Bindings.createDoubleBinding(() ->
                     menu.getLayoutY() + menu.getPrefHeight() + 9, menu.layoutYProperty(), menu.prefHeightProperty()
            ));
        }
    }

    public static boolean isPresent(String parentId) {
        return !CollectionUtils.isEmpty(submenus.getOrDefault(parentId, new ArrayList<>()));
    }
}

package org.mahefa.events;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import org.mahefa.component.Menu;
import org.mahefa.component.MenuItem;
import org.mahefa.controller.MainWindowController;
import org.mahefa.service.algorithm.maze_generator.Randomized;

public class MenuItemEventHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            setOnMouseClickedProperty((MouseEvent) event);
        }
    }

    private void setOnMouseClickedProperty(MouseEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        Menu parent = menuItem.getMenu();

        switch (parent.getId()) {
            case "menu_algorithms":
                algorithmAction(menuItem);
                break;
            case "menu_mazes_patterns":
                mazeGeneratorAction(menuItem);
                break;
            case "menu_clear_board":
                break;
            case "menu_clear_walls_weights":
                break;
            case "menu_clear_path":
                break;
            case "menu_speed":
                speedAction(menuItem);
                break;
            default:
                break;
        }

        event.consume();
    }

    private void algorithmAction(MenuItem menuItem) {
//        descriptionLabel.set("Pick an algorithm and visualize it!");
//        descriptionLabel.set(
//                String.format("{0} is {1} and {2} the shortest path!",
//                        parent.getLabel().getText(),
//                        submenu.isWeighted() ? "weighted" : "unweighted",
//                        submenu.isGuaranteeShortestPath() ? "guarantees" : "does not guarantee"
//                )
//        );
    }

    private void mazeGeneratorAction(MenuItem menuItem) {
        final String menuItemId = menuItem.getId();

        if (menuItemId.equalsIgnoreCase("startButtonAldousBroder")) {
        } else if (menuItemId.equalsIgnoreCase("startButtonPrim")) {
        } else if (menuItemId.equalsIgnoreCase("startButtonRecursiveDivison")) {
        } else if (menuItemId.equalsIgnoreCase("startButtonRecursiveDivisonV")) {
        } else if (menuItemId.equalsIgnoreCase("startButtonRecursiveDivisonH")) {
        } else if (menuItemId.equalsIgnoreCase("startButtonRandom")) {
            new Randomized().generate(MainWindowController.grid);
        }
    }

    private void speedAction(MenuItem menuItem) {
//        final Text label = menuItem.getLabel();
//        submenu.setSelectedSubMenuItemText(label.getText());
    }
}

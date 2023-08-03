package org.mahefa.controller;

import animatefx.animation.ZoomIn;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.mahefa.common.CellStyle.Flag;
import org.mahefa.data.Cell;
import org.mahefa.data.Grid;
import org.mahefa.data.builder.Navbar;
import org.mahefa.events.CellEventHandler;
import org.mahefa.service.algorithm.maze_generator.AldousBroder;
import org.mahefa.service.algorithm.maze_generator.Randomized;
import org.mahefa.service.algorithm.maze_generator.RandomizedPrim;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    @FXML StackPane stackPane;
    @FXML BorderPane borderPane;
    @FXML FlowPane navbarPane;
    @FXML VBox content;
    @FXML HBox legend;
    @FXML HBox description;
    @FXML HBox gridContainer;
    @FXML Pane gridPane;

    @Value("${square.size}") private int squareSize;

    @Autowired Randomized randomized;
    @Autowired AldousBroder aldousBroder;
    @Autowired RandomizedPrim randomizedPrim;

    private Grid grid;
    private Navbar navbar;

    @FXML
    private void initialize() {
        // Bind width and height property
        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
        gridContainer.prefWidthProperty().bind(content.widthProperty());
        gridContainer.prefHeightProperty().bind(content.heightProperty().subtract(legend.getHeight()).subtract(description.getHeight()));
        gridPane.prefWidthProperty().bind(gridContainer.widthProperty());
        gridPane.prefHeightProperty().bind(gridContainer.heightProperty());

        navbar = new Navbar.Builder(navbarPane)
                .setArea(stackPane)
                .build();

        borderPane.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (navbar.getCurrentActiveMenu() == null)
                return;

            Node clickedNode = event.getPickResult().getIntersectedNode();

            if (clickedNode != null && !(clickedNode instanceof HBox))
                clickedNode = clickedNode.getParent();

            // Remove the current active menu if the user clicked elsewhere
            if (
                    clickedNode == null ||
                    clickedNode.getId() == null ||
                    (!clickedNode.getId().startsWith("menu_") && !clickedNode.getId().equalsIgnoreCase("submenu"))
            ) {
                navbar.setMenuToDefault();
            }
        });

        borderPane.addEventFilter(DragEvent.DRAG_OVER, event -> {
            Node sourceNode = (Node) event.getGestureSource();
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasString() && sourceNode != null && sourceNode.getId() != null && sourceNode.getId().startsWith("cell")) {
                Node targetNode = (Node) event.getTarget();

                if (targetNode != null && targetNode.getId() != null && !targetNode.getId().startsWith("cell")) {
                    final String cssClass = dragboard.getString();
                    ((Cell) sourceNode).resetFlag(Flag.valueOf(cssClass));

                    ImageView imageView = (ImageView) ((Cell) sourceNode).getChildren().get(0);
                    imageView.visibleProperty().setValue(true);

                    new ZoomIn(imageView).play();
                }
            }
        });

        // Update grid accordingly to the size of the container
        gridPane.layoutBoundsProperty().addListener((e) -> {
            int width = (int) gridPane.getPrefWidth();
            int height = (int) gridPane.getPrefHeight();

            gridPane.getChildren().clear();

            // Create a grid
            grid = new Grid(width, height, squareSize, (currentCell) -> {
                final Flag currentFlag = currentCell.getFlag();

                if (currentFlag.equals(Flag.START) || currentFlag.equals(Flag.TARGET)) {
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(currentCell.getPrefWidth());
                    imageView.setFitHeight(currentCell.getPrefHeight());
                    imageView.setPickOnBounds(false);
                    imageView.setPreserveRatio(true);
                    imageView.getStyleClass().add("icon");

                    currentCell.getChildren().add(imageView);

                    imageView.managedProperty().bind(imageView.visibleProperty());

                    new ZoomIn(imageView).play();
                }

                gridPane.getChildren().add(currentCell);

                addCellEvent(currentCell);
            });
        });
    }

    private void addCellEvent(Cell cell) {
        CellEventHandler cellEventHandler = new CellEventHandler();

        cell.setOnDragDetected(event -> cellEventHandler.handle(event));
        cell.setOnDragOver(event -> cellEventHandler.handle(event));
        cell.setOnDragDropped(event -> cellEventHandler.handle(event));
    }
}
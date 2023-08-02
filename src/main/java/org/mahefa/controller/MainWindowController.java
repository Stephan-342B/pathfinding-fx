package org.mahefa.controller;

import animatefx.animation.ZoomIn;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import org.mahefa.common.enumerator.Flag;
import org.mahefa.data.Cell;
import org.mahefa.data.Grid;
import org.mahefa.data.Location;
import org.mahefa.data.builder.Navbar;
import org.mahefa.data.builder.TileBuilder;
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
            Node targetNode = (Node) event.getTarget();
            Node sourceNode = (Node) event.getGestureSource();
            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasString() && sourceNode != null && sourceNode.getId() != null && sourceNode.getId().equals("cell")) {
                if (targetNode != null && targetNode.getId() != null && !targetNode.getId().equals("cell")) {
                    final String cssClass = dragboard.getString();

                    Cell sourceCell = (Cell) sourceNode.getUserData();
                    sourceCell.resetFlag(Flag.valueFor(cssClass));

//                event.consume();
                }
            }
        });

        // Update grid accordingly to the size of the container
        gridPane.layoutBoundsProperty().addListener((e) -> {
            int width = (int) gridPane.getPrefWidth();
            int height = (int) gridPane.getPrefHeight();

            gridPane.getChildren().clear();

            // Create a grid
            grid = new Grid(width, height, squareSize, (currentCell, colLen) -> {
                final Location currentLocation = currentCell.getLocation();
                final int currentIndex = (currentLocation.getX() * colLen) + currentLocation.getY();

                // Update tile depending on the current flag
                currentCell.flagProperty().addListener((observableValue, oldFlag, newFlag) -> {
                    Pane currentSquare = (Pane) gridPane.getChildren().get(currentIndex);
                    currentSquare.getStyleClass().clear();
                    currentSquare.getStyleClass().add(newFlag.getCssClassValue());

                    // Update start and target cell position
                    if ((oldFlag.equals(Flag.START) || oldFlag.equals(Flag.TARGET)) && !newFlag.equals(oldFlag)) {
                        ImageView imageView = (ImageView) currentSquare.getChildren().get(0);
                        imageView.visibleProperty().setValue(false);
                    }

                    if (newFlag.equals(Flag.START) || newFlag.equals(Flag.TARGET)) {
                        if (newFlag.equals(Flag.START)) {
                            grid.setStartCell(currentCell);
                        } else if (newFlag.equals(Flag.TARGET)) {
                            grid.setTargetCell(currentCell);
                        }

                        ImageView imageView = (ImageView) currentSquare.getChildren().get(0);
                        imageView.visibleProperty().setValue(true);

                        new ZoomIn(imageView).play();
                    }
                });

                final TileBuilder tileBuilder = new TileBuilder(currentCell.col, currentCell.row)
                        .setSize(squareSize)
                        .setUserData(currentCell);

                gridPane.getChildren().add(tileBuilder.build());
            });
        });
    }
}
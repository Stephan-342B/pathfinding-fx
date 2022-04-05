package org.mahefa.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXNodesList;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.mahefa.data.Cell;
import org.mahefa.data.Grid;
import org.mahefa.service.algorithm.maze_generator.AldousBroder;
import org.mahefa.service.algorithm.maze_generator.Randomized;
import org.mahefa.service.algorithm.maze_generator.RandomizedPrim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

    @FXML StackPane stackPane;
    @FXML BorderPane borderPane;
    @FXML Pane pane;
    @FXML JFXNodesList floater;
    @FXML JFXButton options;

    @Value("${square.size}") private int squareSize;
    @Value("${draw.grid.base.timer}") private int drawGridBaseTimer;

    @Autowired Randomized randomized;
    @Autowired AldousBroder aldousBroder;
    @Autowired RandomizedPrim randomizedPrim;

    private Grid grid;

    @FXML
    private void initialize() {
        // Bind width and height property
        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
        pane.prefWidthProperty().bind(borderPane.widthProperty());
        pane.prefHeightProperty().bind(borderPane.heightProperty());

        boolean fullOfWalls = true;

        // Update grid accordingly to the size of the container
        pane.layoutBoundsProperty().addListener((e) -> {
            int width = (int) pane.getPrefWidth();
            int height = (int) pane.getPrefHeight() - 70; // remove top-bar height

            pane.getChildren().clear();

            grid = new Grid(width, height, squareSize, fullOfWalls, (i, j) -> {
                pane.getChildren().add(addCell(i, j, fullOfWalls));
                return 0;
            });

            addListener();
        });

        options.setOnAction(event -> {
//            randomized.generate(grid);
            aldousBroder.generate(grid);
        });
    }

    private Rectangle addCell(final int x, final int y, final boolean defaultToWall) {
        Rectangle rectangle = new Rectangle(x, y, squareSize, squareSize);
        rectangle.getStyleClass().add("cell");

        if(defaultToWall)
            rectangle.getStyleClass().add("default-wall");

        rectangle.managedProperty().bind(rectangle.cacheProperty());

        return rectangle;
    }

    private void addListener() {
        for(int i = 0; i < this.grid.getRowLen(); i++) {
            for(int j = 0; j < this.grid.getColLen(); j++) {
                Cell cell = this.grid.getCellAt(i, j);
                final int currentIndex = (i * this.grid.getColLen()) + j;

                cell.flagProperty().addListener((observableValue, oldFlag, newFlag) -> {
                    Rectangle rectangle = (Rectangle)pane.getChildren().get(currentIndex);

                    switch(newFlag) {
                        case PATH:
                            rectangle.getStyleClass().removeAll("wall","pointer");
                            rectangle.getStyleClass().add("path");
                            break;
                        case POINTER:
                            rectangle.getStyleClass().removeAll("path","wall");
                            rectangle.getStyleClass().add("pointer");
                            break;
                        case WALL:
                            rectangle.getStyleClass().removeAll("path","pointer");
                            rectangle.getStyleClass().add("wall");
                            break;
                        case VISITED:
                            rectangle.getStyleClass().removeAll("pointer");
                            break;
                    }
                });
            }
        }
    }
}
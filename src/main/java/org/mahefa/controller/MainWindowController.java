package org.mahefa.controller;

import com.jfoenix.controls.JFXNodesList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

    @FXML StackPane stackPane;
    @FXML BorderPane borderPane;
    @FXML Pane pane;
    @FXML JFXNodesList floater;


    private double oldFloaterPosX;
    private double oldFloaterPosY;

    @FXML
    private void initialize() {
        // Bind width and height property
        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
        pane.prefWidthProperty().bind(borderPane.widthProperty());
        pane.prefHeightProperty().bind(borderPane.heightProperty());

        // Update grid accordingly to the size of the container
        pane.layoutBoundsProperty().addListener((e) -> {
            int width = (int) pane.getPrefWidth();
            int height = (int) pane.getPrefHeight() - 70; // remove top-bar height

            makeGrid(width, height);
        });

        floater.setOnMousePressed(event -> {
            oldFloaterPosX = floater.getLayoutX() - event.getSceneX();
            oldFloaterPosY = floater.getLayoutY() - event.getSceneY();
            floater.setCursor(Cursor.MOVE);
        });

        floater.setOnMouseReleased(event -> {
            floater.setCursor(Cursor.HAND);
        });

        floater.setOnMouseDragged(event -> {
            floater.setTranslateX(floater.getLayoutX() + oldFloaterPosX);
            floater.setTranslateY(floater.getLayoutY() + oldFloaterPosY);
        });
    }

    void makeGrid(int width, int height) { ;
        final int squareSize = 25;
        final int gapRow = height % squareSize;
        final int gapCol = width % squareSize;
        width -= gapCol;
        height -= gapRow;

        pane.getChildren().clear();

        for(int i = gapRow / 2; i < height; i += squareSize) {
            for(int j = gapCol / 2; j < width; j += squareSize) {
                Rectangle rectangle = new Rectangle(j, i, squareSize, squareSize);
                rectangle.setId("cell");

                pane.getChildren().add(rectangle);
            }
        }
    }
}

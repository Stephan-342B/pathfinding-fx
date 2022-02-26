package org.mahefa.controller;

import com.jfoenix.controls.JFXNodesList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import org.mahefa.data.Grid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MainWindowController {

    @FXML StackPane stackPane;
    @FXML BorderPane borderPane;
    @FXML Pane pane;
    @FXML JFXNodesList floater;

    @Value("${square.size}")
    private int squareSize;

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

//            makeGrid(width, height);

            pane.getChildren().clear();

            new Grid(width, height, squareSize, (i, j) -> {
                pane.getChildren().add(addCell(i, j));
                return 0;
            });
        });

        // Not working yet
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

    private Rectangle addCell(final int x, final int y) {
        Rectangle rectangle = new Rectangle(x, y, squareSize, squareSize);
        rectangle.setId("cell");

        return rectangle;
    }
}

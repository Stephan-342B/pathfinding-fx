package org.mahefa.data.builder;

import animatefx.animation.ZoomIn;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import org.mahefa.common.enumerator.Flag;
import org.mahefa.data.Cell;

public class TileBuilder {

    private final int layoutX;
    private final int layoutY;
    private int size;
    private Cell userData;

    public TileBuilder(int layoutX, int layoutY) {
        this.layoutX = layoutX;
        this.layoutY = layoutY;
    }

    public TileBuilder setSize(int size) {
        this.size = size;
        return this;
    }

    public TileBuilder setUserData(Cell userData) {
        this.userData = userData;
        return this;
    }

    public Node build() {
        Pane tile = new Pane();
        tile.setId("cell");
        tile.setLayoutX(layoutX);
        tile.setLayoutY(layoutY);
        tile.setPrefWidth(size);
        tile.setPrefHeight(size);
        tile.setPickOnBounds(true);
        tile.setUserData(userData);

        final Flag flag = userData.getFlag();

        if (flag.equals(Flag.START) || flag.equals(Flag.TARGET))
            addImageView(tile, flag);

        addCellEventListener(tile);

        return tile;
    }

    private void addImageView(Pane currentTile, Flag flag) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(currentTile.getPrefWidth());
        imageView.setFitHeight(currentTile.getPrefHeight());
        imageView.setPickOnBounds(false);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add(flag.getCssClassValue());

        currentTile.getChildren().add(imageView);

        imageView.managedProperty().bind(imageView.visibleProperty());

        new ZoomIn(imageView).play();
    }

    private void addCellEventListener(Pane tile) {
        tile.setOnMouseDragOver(event -> {
            javafx.scene.Node currentNode = event.getPickResult().getIntersectedNode();
            Cell currentCell = (Cell) currentNode.getUserData();
            Flag currentFlag = currentCell.getFlag();

            if (!currentFlag.equals(Flag.START) && !currentFlag.equals(Flag.TARGET)) {
                currentCell.setFlag(Flag.WALL);
            }
        });

        tile.setOnDragDetected(event -> {
            Cell currentCell = (Cell) tile.getUserData();

            if (tile.getChildren().isEmpty())
                return;

            ImageView currentImageView = (ImageView) tile.getChildren().get(0);
            Image currentImage = currentImageView.getImage();

            try {
                final String cssClassValue = currentCell.getFlag().getCssClassValue();
                Dragboard dragboard = tile.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString(cssClassValue);
                dragboard.setContent(content);

                ImageView dragThumbnailView = new ImageView(currentImage);
                dragThumbnailView.setScaleX(0.6);
                dragThumbnailView.setScaleY(0.6);
                dragThumbnailView.setPreserveRatio(true);
                dragboard.setDragView(dragThumbnailView.snapshot(null, null));

                currentCell.revertFlag();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        tile.setOnDragOver(event -> {
            Node sourceNode = (Node) event.getGestureSource();

            if (sourceNode instanceof Pane) {
                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        tile.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();

            Node sourceNode = (Node) event.getGestureSource();
            Node targetNode = (Node) event.getGestureTarget();

            if (dragboard.hasString() && sourceNode instanceof Pane) {
                final String cssClass = dragboard.getString();
                Flag currentFlag = Flag.valueFor(cssClass);

                // There is already an image inside the current tile, so we need to keep them unchanged
                if (!tile.getChildren().isEmpty()) {
                    Cell sourceCell = (Cell) sourceNode.getUserData();
                    sourceCell.resetFlag(currentFlag);
                    return;
                }

                // Set the current image into the targetNode
                ImageView imageView = (ImageView) ((Pane) sourceNode).getChildren().remove(0);
                ((Pane) targetNode).getChildren().add(imageView);

                // Update flag
                Cell currentCell = (Cell) tile.getUserData();
                currentCell.setFlag(currentFlag);
            }

            event.setDropCompleted(true);
            event.consume();
        });
    }
}

package org.mahefa.events;

import animatefx.animation.ZoomIn;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import org.mahefa.common.CellStyle;
import org.mahefa.component.Cell;

public class CellEventHandler implements EventHandler<Event> {

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
            setOnDragDetected((MouseEvent) event);
        } else if (event.getEventType() == DragEvent.DRAG_OVER) {
            setOnDragOver((DragEvent) event);
        } else if (event.getEventType() == DragEvent.DRAG_DROPPED) {
            setOnDragDropped((DragEvent) event);
        }
    }

    private void setOnDragDetected(MouseEvent event) {
        Cell cell = (Cell) event.getSource();

        if (cell.getChildren().isEmpty())
            return;

        ImageView currentImageView = (ImageView) cell.getChildren().get(0);
        Image currentImage = currentImageView.getImage();

        try {
            Dragboard dragboard = cell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(cell.getFlag().name());
            dragboard.setContent(content);

            ImageView dragThumbnailView = new ImageView(currentImage);
            dragThumbnailView.setScaleX(0.6);
            dragThumbnailView.setScaleY(0.6);
            dragThumbnailView.setPreserveRatio(true);
            dragboard.setDragView(dragThumbnailView.snapshot(null, null));

            cell.revertFlag();
            currentImageView.visibleProperty().setValue(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        event.consume();
    }

    private void setOnDragOver(DragEvent event) {
        Node sourceNode = (Node) event.getGestureSource();

        if (sourceNode instanceof Pane) {
            event.acceptTransferModes(TransferMode.MOVE);
        }

        event.consume();
    }

    private void setOnDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        Cell sourceNode = (Cell) event.getGestureSource();
        Cell targetNode = (Cell) event.getGestureTarget();

        if (dragboard.hasString() && sourceNode instanceof Pane) {
            final String cssClass = dragboard.getString();
            CellStyle.Flag currentFlag = CellStyle.Flag.valueOf(cssClass);

            // Set the current image into the targetNode
            ImageView imageView = (ImageView) sourceNode.getChildren().remove(0);
            imageView.visibleProperty().setValue(true);

            // There is already an image inside the current tile, so we need to keep them unchanged
            if (!targetNode.getChildren().isEmpty()) {
                sourceNode.resetFlag(currentFlag);
                sourceNode.getChildren().add(imageView);
            } else {
                // Update flag
                targetNode.setFlag(currentFlag);
                targetNode.getChildren().add(imageView);
            }

            new ZoomIn(imageView).play();
        }

        event.setDropCompleted(true);
        event.consume();
    }
}

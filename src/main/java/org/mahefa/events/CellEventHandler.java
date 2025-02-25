package org.mahefa.events;

import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import org.mahefa.common.enumerator.NodeType;
import org.mahefa.component.Cell;

import static org.mahefa.common.CellStyle.Flag;

public class CellEventHandler implements EventHandler<Event> {

    private boolean isMousePressed = false;

    @Override
    public void handle(Event event) {
        if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
            setOnMouseClicked((MouseEvent) event);
        } else if ( event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            setOnMousePressed((MouseEvent) event);
        } else if ( event.getEventType() == MouseEvent.MOUSE_RELEASED) {
            setOnMouseReleased((MouseEvent) event);
        } else if ( event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            setOnMouseDragged((MouseEvent) event);
        } else if (event.getEventType() == MouseEvent.DRAG_DETECTED) {
            setOnDragDetected((MouseEvent) event);
        } else if (event.getEventType() == DragEvent.DRAG_OVER) {
            setOnDragOver((DragEvent) event);
        } else if (event.getEventType() == DragEvent.DRAG_DROPPED) {
            setOnDragDropped((DragEvent) event);
        }
    }

    private void setOnMouseClicked(MouseEvent event) {
        Cell currentCell = (Cell) event.getSource();
        setWallNode(currentCell);
    }

    private void setOnMousePressed(MouseEvent event) {
        isMousePressed = true;
        Cell currentCell = (Cell) event.getSource();
        setWallNode(currentCell);
    }

    private void setOnMouseReleased(MouseEvent event) {
        isMousePressed = false;
    }

    private void setOnMouseDragged(MouseEvent event) {
        if (isMousePressed) {
            Cell currentCell = (Cell) event.getSource();
            setWallNode(currentCell);
        }
    }

    private void setOnDragDetected(MouseEvent event) {
        try {
            Cell currentCell = (Cell) event.getSource();
            NodeType currentNodeType = currentCell.getNodeType();

            if (currentNodeType.equals(NodeType.NONE))
                return;

            ImageView currentImageView = (ImageView) currentCell.getChildren().get(0);
            Image currentImage = currentImageView.getImage();

            Dragboard dragboard = currentCell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(currentCell.getUserData().toString());
            dragboard.setContent(content);

            ImageView dragThumbnailView = new ImageView(currentImage);
            dragThumbnailView.setScaleX(0.6);
            dragThumbnailView.setScaleY(0.6);
            dragThumbnailView.setPreserveRatio(true);
            dragboard.setDragView(dragThumbnailView.snapshot(null, null));

            new ZoomOut(currentImageView).play();
            currentImageView.visibleProperty().setValue(false);
            currentCell.revertFlag();
        } catch (Exception e) {
            e.printStackTrace();
        }

        event.consume();
    }

    private void setOnDragOver(DragEvent event) {
        Cell sourceNode = (Cell) event.getGestureSource();

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
            // There is already an image inside the current tile, so we need to keep them unchanged
            if (!targetNode.getNodeType().equals(NodeType.NONE)) {
                ImageView imageView = (ImageView) sourceNode.getChildren().get(0);
                imageView.visibleProperty().setValue(true);
                new ZoomIn(imageView).play();
            } else {
                targetNode.setNodeType(sourceNode.getNodeType());
            }
        }

        event.setDropCompleted(true);
        event.consume();
    }

    private void setWallNode(Cell currentCell) {
        NodeType nodeType = currentCell.getNodeType();

        if (nodeType.equals(NodeType.NONE)) {
            Flag currentFlag = currentCell.getFlag();

            if (!currentFlag.equals(Flag.WALL_NODE)) {
                currentCell.setFlag(Flag.WALL_NODE);
            } else {
                if (!currentCell.revertFlag()) {
                    currentCell.setFlag(Flag.NONE);
                }
            }
        }
    }
}

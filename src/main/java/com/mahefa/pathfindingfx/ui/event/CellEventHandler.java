package com.mahefa.pathfindingfx.ui.event;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import com.mahefa.pathfindingfx.domain.enumerator.NodeType;
import com.mahefa.pathfindingfx.ui.animation.NodeAnimations;
import com.mahefa.pathfindingfx.ui.component.Cell;

import java.util.function.BiConsumer;

import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag;

public class CellEventHandler implements EventHandler<Event> {

    private boolean isMousePressed = false;

    // Notified on each DRAG_OVER while a start/target node is being dragged: (dragged node type, cell
    // under the cursor). Lets the controller recompute the path live as the node moves.
    private final BiConsumer<NodeType, Cell> onDragOverSpecial;
    // Notified when a start/target drag ends (drop), so the controller can repaint for the final position.
    private final Runnable onDragFinished;

    public CellEventHandler() {
        this(null, null);
    }

    public CellEventHandler(BiConsumer<NodeType, Cell> onDragOverSpecial, Runnable onDragFinished) {
        this.onDragOverSpecial = onDragOverSpecial;
        this.onDragFinished = onDragFinished;
    }

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

            // Live path preview: while a start/target node is dragged, tell the controller which cell the
            // cursor is over so it can recompute instantly. The node itself only moves on drop.
            if (sourceNode.isSpecialNode() && onDragOverSpecial != null && event.getSource() instanceof Cell hovered) {
                onDragOverSpecial.accept(sourceNode.getNodeType(), hovered);
            }
        }

        event.consume();
    }

    private void setOnDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();

        Cell sourceNode = (Cell) event.getGestureSource();
        Cell targetNode = (Cell) event.getGestureTarget();

        if (dragboard.hasString() && sourceNode instanceof Pane) {
            // There is already an image inside the current tile, so we need to keep them unchanged
            if (targetNode.isSpecialNode()) {
                ImageView imageView = (ImageView) sourceNode.getChildren().get(0);
                imageView.visibleProperty().setValue(true);
                NodeAnimations.pop(imageView);
            } else {
                targetNode.setNodeType(sourceNode.getNodeType());
            }
        }

        // Repaint the path for the committed position (also clears a stale live preview on an invalid drop).
        if (onDragFinished != null) {
            onDragFinished.run();
        }

        event.setDropCompleted(true);
        event.consume();
    }

    private void setWallNode(Cell currentCell) {
        if (!currentCell.isSpecialNode()) {
            Flag currentFlag = currentCell.getFlag();

            if (!currentFlag.equals(Flag.WALL_NODE)) {
                currentCell.setFlag(Flag.WALL_NODE);
            } else {
                currentCell.revertFlag();
            }
        }
    }
}

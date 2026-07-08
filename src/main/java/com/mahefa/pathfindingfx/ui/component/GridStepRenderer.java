package com.mahefa.pathfindingfx.ui.component;

import com.mahefa.pathfindingfx.algorithm.step.Step;
import com.mahefa.pathfindingfx.domain.Location;
import com.mahefa.pathfindingfx.domain.enumerator.NodeType;
import com.mahefa.pathfindingfx.ui.animation.NodeAnimations;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.function.Consumer;

import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag;

/**
 * The production side of the record/replay split: applies {@link Step}s to the real {@link Grid} on
 * the FX thread. The ONLY thing that touches {@code Cell}s — algorithms stay UI-free. Called from
 * the {@code StepPlayer}'s {@code AnimationTimer}, so all mutations happen on the FX thread. A fresh
 * instance is used per run, so the PATH arrow state below resets each time.
 * <p>
 * Most types are a stateless {@link com.mahefa.pathfindingfx.algorithm.step.StepType}→{@link Flag}
 * map. {@code PATH} reproduces the original A* "drawback": a single arrow {@code ImageView} travels
 * to the target, rotating per cell (angle carried on the {@link Step}), colouring the path behind it.
 * NodeType (START/TARGET) is never touched, so special cells keep their identity.
 */
public final class GridStepRenderer implements Consumer<Step> {

    private final Grid grid;

    // When true, render the final state only — no travelling arrow, and CURRENT collapses to VISITED.
    // Used for the instant recompute while dragging start/target (see GridService.recomputeInstant).
    private final boolean arrowless;

    // PATH ("drawback") state: one arrow that walks the path to the target.
    private ImageView arrow;
    private Cell priorPathCell;

    public GridStepRenderer(Grid grid) {
        this(grid, false);
    }

    public GridStepRenderer(Grid grid, boolean arrowless) {
        this.grid = grid;
        this.arrowless = arrowless;
    }

    @Override
    public void accept(Step step) {
        Location location = step.location();
        Cell cell = grid.getCellAt(location.getRow(), location.getCol());

        if (arrowless) {
            // Instant recompute: just colour the cell to its final state, no arrow. Leave the special
            // start/target cells untouched so they keep their identity.
            if (!cell.isSpecialNode()) {
                switch (step.type()) {
                    case CURRENT, VISITED -> cell.setFlag(Flag.VISITED);
                    case OPEN -> cell.setFlag(Flag.NONE);
                    case WALL -> cell.setFlag(Flag.WALL_NODE);
                    case PATH -> cell.setFlag(Flag.SHORTEST_PATH_NODE);
                }
            }
            return;
        }

        switch (step.type()) {
            case CURRENT -> cell.setFlag(Flag.CURRENT);
            case VISITED -> cell.setFlag(Flag.VISITED);
            case OPEN -> cell.setFlag(Flag.NONE);
            case WALL -> cell.setFlag(Flag.WALL_NODE);
            case PATH -> renderPath(cell, step.angle());
        }
    }

    // Ported from AStar.drawback(): move a single rotating arrow from cell to cell along the path.
    private void renderPath(Cell cell, double angle) {
        if (arrow == null) {
            cell.getStyleClass().add("transparent");
            arrow = new ImageView(new Image("/icons/triangletwo-up.png"));
        }

        // Remove the arrow from its previous cell by reference, not by index — the cell may hold other
        // children, so removing index 0 could strand the arrow.
        if (priorPathCell != null) {
            priorPathCell.getChildren().remove(arrow);
        }

        arrow.setRotate(angle);

        // Path cells colour yellow via CSS (no scaling, so the gridline stays put); the pop bounce rides
        // on the arrow — or, at the special nodes, on their own icon — so it stays cohesive.
        if (cell.getNodeType() == NodeType.TARGET) {
            // Arrow has arrived: the target joins the path (yellow) and the arrow merges into its icon.
            // Until now it showed VISITED (turquoise) from when the search reached it.
            cell.setFlag(Flag.SHORTEST_PATH_NODE);
            if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView imageView) {
                imageView.setImage(arrow.getImage());
                imageView.setRotate(angle);
                NodeAnimations.pathPop(imageView);
            }
        } else if (!cell.isSpecialNode()) {
            cell.setFlag(Flag.SHORTEST_PATH_NODE);
            cell.getChildren().add(arrow);
            NodeAnimations.pathPop(arrow);
        } else if (!cell.getChildren().isEmpty() && cell.getChildren().get(0) instanceof ImageView imageView) {
            // Start cell: keeps its own icon (and the "transparent" style); bounce it too.
            cell.setFlag(Flag.SHORTEST_PATH_NODE);
            NodeAnimations.pathPop(imageView);
        }

        priorPathCell = cell;
    }
}

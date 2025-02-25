package org.mahefa.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Pane;
import org.mahefa.common.CellStyle.*;
import org.mahefa.common.enumerator.NodeType;

import java.util.Objects;
import java.util.Stack;

import static org.mahefa.common.CellStyle.*;

public class Cell extends Pane {

    private int weight;

    private Location location;
    private ObjectProperty<NodeType> nodeType = new SimpleObjectProperty<>(NodeType.NONE);
    private Stack<Flag> oldFlag = new Stack<>();
    private ObjectProperty<Flag> flag = new SimpleObjectProperty<>() {
        @Override
        public void invalidated() {
            pseudoClassStateChanged(WALL_NODE_PSEUDO_CLASS, false);
            pseudoClassStateChanged(POINTER_PSEUDO_CLASS, false);
            pseudoClassStateChanged(PATH_NODE_PSEUDO_CLASS, false);
            pseudoClassStateChanged(VISITED_PSEUDO_CLASS, false);

            switch (get()) {
                case WALL_NODE:
                    pseudoClassStateChanged(WALL_NODE_PSEUDO_CLASS, true);
                    break;
                case POINTER:
                    pseudoClassStateChanged(POINTER_PSEUDO_CLASS, true);
                    break;
                case PATH_NODE:
                    pseudoClassStateChanged(PATH_NODE_PSEUDO_CLASS, true);
                    break;
                case VISITED:
                    pseudoClassStateChanged(VISITED_PSEUDO_CLASS, true);
                    break;
                default:
                    break;
            }
        }
    };

    public Cell(double posX, double posY, int row, int col, double gridSize) {
        super();
        getStyleClass().add("cell");

        String fxId = "cell_" + row + "_" + col;

        setUserData(fxId);
        setLayoutX(posY);
        setLayoutY(posX);
        setPrefWidth(gridSize);
        setWidth(gridSize);
        setPrefHeight(gridSize);
        setHeight(gridSize);
        setPickOnBounds(true);

        this.location = new Location(row, col);
        this.weight = 0;
    }

    public Location getLocation() {
        return location;
    }

    public NodeType getNodeType() {
        return nodeType.get();
    }

    public ObjectProperty<NodeType> nodeTypeProperty() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType.set(nodeType);
    }

    public Flag getFlag() {
        return flag.get();
    }

    public void setFlag(Flag flag) {
        Flag currentFlag = this.flag.get();

        if (currentFlag != null)
            this.oldFlag.push(this.flag.get());

        this.flag.setValue(flag);
    }

    public boolean revertFlag() {
        boolean isReverted = false;

        if (!oldFlag.isEmpty()) {
            this.flag.setValue(oldFlag.pop());
            isReverted = true;
        }

        return isReverted;
    }

    public void resetFlag(Flag defaultFlag) {
        oldFlag = new Stack<>();
        flag.setValue(defaultFlag);
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return Objects.equals(getLocation(), cell.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocation());
    }
}

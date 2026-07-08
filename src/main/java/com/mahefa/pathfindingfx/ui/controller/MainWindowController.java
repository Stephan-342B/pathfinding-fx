package com.mahefa.pathfindingfx.ui.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import com.mahefa.pathfindingfx.ui.style.CellStyle.Flag;
import com.mahefa.pathfindingfx.domain.enumerator.*;
import com.mahefa.pathfindingfx.exception.MissingAlgorithmException;
import com.mahefa.pathfindingfx.exception.PathFindingException;
import com.mahefa.pathfindingfx.ui.component.*;
import com.mahefa.pathfindingfx.domain.*;
import com.mahefa.pathfindingfx.ui.animation.NodeAnimations;
import com.mahefa.pathfindingfx.ui.event.CellEventHandler;
import com.mahefa.pathfindingfx.config.AppProperties;
import com.mahefa.pathfindingfx.service.GridService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.mahefa.pathfindingfx.ui.style.StateStyle.State;

@Component
public class MainWindowController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainWindowController.class);

    @FXML StackPane stackPane;
    @FXML BorderPane borderPane;
    @FXML Navbar navbar;
    @FXML VBox content;
    @FXML HBox legend;
    @FXML HBox description;
    @FXML Label descriptionLabel;
    @FXML HBox gridContainer;
    @FXML Pane gridPane;

    @Autowired private AppProperties appProperties;

    private MenuBar menuBar;
    private Button btnPlay;
    public GridService gridService;

    // Live-drag path recompute: suppress the reveal animations during an instant repaint, and remember
    // the last previewed cell so we only recompute when the dragged node actually changes cell.
    private boolean suppressReveal = false;
    private Cell lastPreviewCell;

    public ObjectProperty<AnimationSpeed> currentSpeed = new SimpleObjectProperty<>();

    @FXML
    private void initialize() {
        // Bind width and height property
        borderPane.prefWidthProperty().bind(stackPane.widthProperty());
        borderPane.prefHeightProperty().bind(stackPane.heightProperty());
        navbar.prefWidthProperty().bind(borderPane.prefWidthProperty());
        gridContainer.prefWidthProperty().bind(content.widthProperty());
        gridContainer.prefHeightProperty().bind(
                content.heightProperty().subtract(legend.heightProperty())
                .subtract(description.heightProperty()).subtract(15)
        );
        gridPane.prefWidthProperty().bind(gridContainer.widthProperty());
        gridPane.prefHeightProperty().bind(gridContainer.heightProperty());

        menuBar = navbar.getMenuBar();
        btnPlay = menuBar.getBtnPlay();

        // Listeners
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
                navbar.setCurrentActiveMenu(null);
            }
        });

        // Navbar
        navbar.currentActiveMenuProperty().addListener((observableValue, oldMenu, currentMenu) -> {
//            ParallelTransition parallelTransition = new ParallelTransition();

            if (oldMenu != null) {
                oldMenu.getStyleClass().removeAll("active-drop-down", "active");

                if (oldMenu.hasItems()) {
                    stackPane.getChildren().remove(1);
                }
//                parallelTransition.getChildren().add(new MenuTransition(Duration.millis(50), currentMenu, MenuTransition.Style.FADE_OUT));
            }

            if (currentMenu != null) {
                if (currentMenu.hasItems()) {
                    final Submenu submenu = new Submenu(currentMenu);
                    stackPane.getChildren().add(1, submenu);
                    stackPane.setAlignment(submenu, Pos.TOP_LEFT);

                    currentMenu.getStyleClass().add("active-drop-down");
                } else {
                    State currentState = currentMenu.getCurrentState();

                    // Block menu when the service is running
                    if (currentState.equals(State.READY))
                        menuAction(currentMenu);

                    currentMenu.getStyleClass().add("active");
                }

//                parallelTransition.getChildren().add(new MenuTransition(Duration.millis(50), currentMenu, MenuTransition.Style.FADE_IN));
            }

//            parallelTransition.play();
        });

        // Menu listener
        menuBar.getMenus().forEach(currentMenu -> addMenuListener(currentMenu));

        // Button
        btnPlay.selectedAlgorithmProperty().addListener((observableValue, algorithm, t1) -> updateBtnPlayText(t1));
        btnPlay.setOnMouseClicked(event -> execute());

        // Update grid accordingly to the size of the container
        gridPane.layoutBoundsProperty().addListener((e) -> {
            Grid newGrid = buildGrid(gridPane.getPrefWidth(), gridPane.getPrefHeight());
            gridService = new GridService(newGrid, appProperties.getNarrative().newLogCleaner());
            gridService.updateSpeed(currentSpeed.get());

            // Carry the current config over to the fresh service. Resizing (e.g. maximising) rebuilds the
            // grid and therefore the service, so the already-selected algorithm must be re-applied —
            // otherwise Visualize reports "Pick an Algorithm!" even though the button still shows one.
            PathFindingAlgorithm selectedAlgorithm = btnPlay.selectedAlgorithmProperty().get();
            if (selectedAlgorithm != null) {
                gridService.setPathAlgorithm(selectedAlgorithm);
            }

            // Bind the button's style property to the service's current state property
            btnPlay.currentStateProperty().bind(
                   Bindings.when(gridService.isReadyProperty()).then(State.READY).otherwise(State.BLOCKED)
            );
        });

        currentSpeed.addListener((observableValue, animationSpeed, t1) -> gridService.updateSpeed(t1));
    }

    private Grid buildGrid(double width, double height) {
        // Clear existing grid
        gridPane.getChildren().clear();

        // Create a grid sized to the actual pane, so the grid centers with equal margins
        Grid grid = new Grid(width, height, appProperties.getGrid().getSize(), (currentCell) -> {
            if (currentCell.isSpecialNode()) {
                NodeType currentNodeType = currentCell.getNodeType();

                ImageView imageView = new ImageView();
                imageView.setFitWidth(currentCell.getPrefWidth());
                imageView.setFitHeight(currentCell.getPrefHeight());
                imageView.setPickOnBounds(false);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("icon");

                currentCell.getChildren().add(imageView);
                currentCell.setId((currentNodeType.equals(NodeType.START)) ? "start" : "target");

                imageView.managedProperty().bind(imageView.visibleProperty());

                NodeAnimations.popIcon(imageView);
            }

            gridPane.getChildren().add(currentCell);
            addCellEvent(currentCell);
            addCellListener(currentCell);
        });

        // Init listener
        grid.startCellProperty().addListener((observableValue, cell, t1) -> switchNodeType(t1, cell));
        grid.targetCellProperty().addListener((observableValue, cell, t1) -> switchNodeType(t1, cell));

        return grid;
    }

    private void addMenuListener(Menu currentMenu) {
        String currentMenuId = currentMenu.getId();

        // Bind state
        currentMenu.currentStateProperty().bind(btnPlay.currentStateProperty());

        // Set default value
        if (currentMenu.hasItems()) {
            ObjectProperty<MenuItem> menuItemObjectProperty = currentMenu.selectedItemProperty();

            if (menuItemObjectProperty.getValue() != null) {
                updateSpeed(currentMenu, menuItemObjectProperty.getValue());
            }
        }

        currentMenu.selectedItemProperty().addListener((observableValue, menuItem, t1) -> {
            MenuItem selectedMenuItem = t1;

            if (selectedMenuItem != null) {
                String menuItemId = selectedMenuItem.getId();

                switch (currentMenuId) {
                    case "menu_algorithms":
                        selectAlgorithm(selectedMenuItem, menuBar.getBtnPlay());
                        break;
                    case "menu_mazes_patterns":
                        runMazeGenerator(currentMenu, menuItemId);
                        break;
                    case "menu_speed":
                        updateSpeed(currentMenu, selectedMenuItem);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void addCellEvent(Cell cell) {
        CellEventHandler cellEventHandler = new CellEventHandler(this::previewPathDuringDrag, this::finalizePathAfterDrag);

//        cell.setOnMouseClicked(cellEventHandler::handle);
        cell.setOnMousePressed(cellEventHandler::handle);
        cell.setOnMouseReleased(cellEventHandler::handle);
        cell.setOnMouseDragged(cellEventHandler::handle);
        cell.setOnDragDetected(cellEventHandler::handle);
        cell.setOnDragOver(cellEventHandler::handle);
        cell.setOnDragDropped(cellEventHandler::handle);
    }

    private void addCellListener(Cell cell) {
        cell.nodeTypeProperty().addListener((observableValue, oldType, currentType) -> {
            if (currentType.equals(NodeType.START)) {
                gridService.getGrid().setStartCell(cell);
            } else if (currentType.equals(NodeType.TARGET)) {
                gridService.getGrid().setTargetCell(cell);
            }
        });

        // Reveal animations are native javafx (no jfxanimation): a scale pulse + colour morph for
        // visited, a scale pulse for wall/shortest-path. Simple colours come from CSS. On any other
        // flag (NONE/CURRENT) we release the visited morph's programmatic background so CSS applies.
        cell.flagProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case WALL_NODE -> NodeAnimations.pop(cell);
                // During a live drag recompute we skip the 1.5s pulse and fill the visited cell instantly.
                case VISITED -> { if (suppressReveal) NodeAnimations.visitedInstant(cell); else NodeAnimations.visited(cell); }
                case SHORTEST_PATH_NODE -> NodeAnimations.shortestPath(cell);
                default -> NodeAnimations.reset(cell);
            }
        });
    }

    /**
     * Live path preview while dragging a start/target node (matches the original): recompute and repaint
     * the visited cells + shortest path instantly as the cursor moves, without the reveal animations.
     * Only active once a path has been run ({@code pathDisplayed}); throttled to actual cell changes.
     */
    private void previewPathDuringDrag(NodeType draggedType, Cell hovered) {
        if (gridService == null || !gridService.isPathDisplayed() || hovered == lastPreviewCell) {
            return;
        }
        if (hovered.isSpecialNode() || hovered.getFlag() == Flag.WALL_NODE) {
            return;
        }
        lastPreviewCell = hovered;
        recomputeInstant(draggedType, hovered.getLocation());
    }

    // Recompute with the dragged endpoint at `dragged` (the other endpoint stays where the grid has it),
    // suppressing the reveal animations so the repaint is instant.
    private void recomputeInstant(NodeType draggedType, Location dragged) {
        Grid grid = gridService.getGrid();
        Location start = (draggedType == NodeType.START) ? dragged : grid.getStartCell().getLocation();
        Location target = (draggedType == NodeType.TARGET) ? dragged : grid.getTargetCell().getLocation();
        suppressReveal = true;
        try {
            gridService.recomputeInstant(start, target);
        } finally {
            suppressReveal = false;
        }
    }

    private void switchNodeType(Cell newCell, Cell currentCell) {
        String cssId = currentCell.getId();
        NodeType currentNodeType = currentCell.getNodeType();
        Node currentImageView = currentCell.getChildren().remove(0);

        // Turn on visibility
        currentImageView.visibleProperty().setValue(true);

        currentCell.setNodeType(NodeType.NONE);
        currentCell.setId(null);
        newCell.setId(cssId);
        newCell.setFlag(Flag.NONE);
        newCell.setNodeType(currentNodeType);
        newCell.getChildren().add(currentImageView);

        NodeAnimations.popIcon(currentImageView);
    }

    /**
     * Called when a start/target drag ends (drop). Repaints the path instantly for the final, committed
     * start/target positions — this also corrects the last live preview if the drop was invalid (dropped
     * on another node) and the node snapped back.
     */
    private void finalizePathAfterDrag() {
        lastPreviewCell = null;
        if (gridService == null || !gridService.isPathDisplayed()) {
            return;
        }
        Grid grid = gridService.getGrid();
        suppressReveal = true;
        try {
            gridService.recomputeInstant(grid.getStartCell().getLocation(), grid.getTargetCell().getLocation());
        } finally {
            suppressReveal = false;
        }
    }

    private void updateSpeed(Menu parent, MenuItem currentMenuItem) {
        String speedId = currentMenuItem.getId();
        currentSpeed.setValue(AnimationSpeed.valueOf(speedId));

        parent.textProperty().setValue("Speed: " + currentMenuItem.getText());
    }

    private void runMazeGenerator(Menu currentMenu, String algorithm) {
        if (gridService == null)
            throw new PathFindingException("Service not instantiated properly");

        gridService.generateMaze(MazeAlgorithm.valueOf(algorithm));

        // Release
        currentMenu.selectedItemProperty().setValue(null);
    }

    private void selectAlgorithm(MenuItem menuItem, Button button) {
        TextFlow textFlow = new TextFlow();

        // Create regular text
        Text regularText = new Text(menuItem.getText() + " is ");

        // Create bold texts
        Text weightedText = new Text(menuItem.isWeighted() ? "weighted" : "unweighted");
        weightedText.getStyleClass().add("boldItalic");
        Text guaranteesText = new Text(menuItem.isGuaranteeShortestPath() ? "guarantees" : "does not guarantee");
        guaranteesText.getStyleClass().add("boldItalic");

        // Add the regular and bold texts to the TextFlow
        textFlow.getChildren().addAll(regularText, weightedText, new Text(" and "), guaranteesText, new Text(" the shortest path!"));

        // Set the TextFlow as the content of the Label
        descriptionLabel.textProperty().set("");
        descriptionLabel.setGraphic(textFlow);

        // Set current algorithm
        PathFindingAlgorithm algorithm = PathFindingAlgorithm.valueOf(menuItem.getId());
        button.selectedAlgorithmProperty().setValue(algorithm);
    }

    private void updateBtnPlayText(PathFindingAlgorithm algorithm) {
        String btnTxtArg = "Visualize";

        switch (algorithm) {
            case DIJKSTRA:
                btnTxtArg += " Dijkstra's";
                break;
            case A_STAR:
                btnTxtArg += " A*";
                break;
            case BREADTH_FIRST_SEARCH:
                btnTxtArg += " BFS";
                break;
            case DEPTH_FIRST_SEARCH:
                btnTxtArg += " DFS";
                break;
            default:
                btnTxtArg = "";
                break;
        }

        btnPlay.getLabel().setText(btnTxtArg + "!");

        // Update service
        gridService.setPathAlgorithm(algorithm);
    }

    private void menuAction(Menu currentMenu) {
        switch (currentMenu.getId()) {
            case "menu_clear_board":
                gridService.clearBoard();
                break;
            case "menu_clear_walls_weights":
                gridService.clearWallWeight();
                break;
            case "menu_clear_path":
                gridService.clearPath();
                break;
            default:
                break;
        }
    }

    private void execute() {
        try {
            if (gridService.isReady()) {
                gridService.findPath();
            }
        } catch (Exception e) {
            if (e instanceof MissingAlgorithmException) {
                btnPlay.getLabel().setText(e.getMessage());
            } else {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
            }

            gridService.cancelRunningWorkers();
        }
    }
}
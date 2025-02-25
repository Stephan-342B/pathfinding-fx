package org.mahefa.controller;

import animatefx.animation.ZoomIn;
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
import org.mahefa.common.CellStyle.Flag;
import org.mahefa.common.enumerator.*;
import org.mahefa.common.exceptions.MissingAlgorithmException;
import org.mahefa.common.exceptions.PathFindingException;
import org.mahefa.component.*;
import org.mahefa.events.CellEventHandler;
import org.mahefa.service.GridService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static org.mahefa.common.StateStyle.State;

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

    @Value("${grid.size}") private double gridSize;

    private MenuBar menuBar;
    private Button btnPlay;
    public GridService gridService;

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
            gridService = new GridService(newGrid);
            gridService.updateSpeed(currentSpeed.get());

            // Bind the button's style property to the service's current state property
            btnPlay.currentStateProperty().bind(
                   Bindings.when(gridService.isReadyProperty()).then(State.READY).otherwise(State.BLOCKED)
            );
        });

        currentSpeed.addListener((observableValue, animationSpeed, t1) -> gridService.updateSpeed(t1));
    }

    private Grid buildGrid(double width, double height) {
        double defaultPadding = 5.0;

        // Clear existing grid
        gridPane.getChildren().clear();

        // Create a grid
        Grid grid = new Grid(width + defaultPadding, height + defaultPadding, gridSize, (currentCell) -> {
            NodeType currentNodeType = currentCell.getNodeType();

            if (!currentNodeType.equals(NodeType.NONE)) {
                ImageView imageView = new ImageView();
                imageView.setFitWidth(currentCell.getPrefWidth());
                imageView.setFitHeight(currentCell.getPrefHeight());
                imageView.setPickOnBounds(false);
                imageView.setPreserveRatio(true);
                imageView.getStyleClass().add("icon");

                currentCell.getChildren().add(imageView);
                currentCell.setId((currentNodeType.equals(NodeType.START)) ? "start" : "target");

                imageView.managedProperty().bind(imageView.visibleProperty());

                new ZoomIn(imageView).play();
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
        CellEventHandler cellEventHandler = new CellEventHandler();

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
    }

    private void switchNodeType(Cell newCell, Cell currentCell) {
        String cssId = currentCell.getId();
        Node currentImageView = currentCell.getChildren().remove(0);

        // Turn on visibility
        currentImageView.visibleProperty().setValue(true);

        currentCell.setNodeType(NodeType.NONE);
        currentCell.setId(null);
        newCell.setId(cssId);
        newCell.setFlag(Flag.NONE);
        newCell.getChildren().add(currentImageView);

        new ZoomIn(currentImageView).play();
    }

    private void updateSpeed(Menu parent, MenuItem currentMenuItem) {
        String speedId = currentMenuItem.getId();
        currentSpeed.setValue(AnimationSpeed.valueOf(speedId));

        parent.textProperty().setValue("Speed: " + currentMenuItem.getText());
    }

    private void runMazeGenerator(Menu currentMenu, String algorithm) {
        if (gridService == null)
            throw new PathFindingException("Service not instantiated properly");

        gridService.getMazeService().setAlgorithm(MazeAlgorithm.valueOf(algorithm));
        gridService.getMazeService().run();

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
        gridService.getRouteFinderService().setAlgorithm(algorithm);
    }

    private void menuAction(Menu currentMenu) {
        switch (currentMenu.getId()) {
            case "menu_clear_board":
                clearBoard();
                break;
            case "menu_clear_walls_weights":
                // TODO
                break;
            case "menu_clear_path":
                clearPath();
                break;
            default:
                break;
        }
    }

    private void execute() {
        try {
            if (gridService.isReady()) {
                gridService.getRouteFinderService().run();
            }
        } catch (Exception e) {
            if (e instanceof MissingAlgorithmException) {
                btnPlay.getLabel().setText(e.getMessage());
            } else {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
            }

            gridService.setCurrentState(ServiceState.FAILED);
        }
    }

    private void clearBoard() {
        gridService.clearBoard();
    }

    private void clearPath() {
        gridService.clearPath();
    }
}
package org.mahefa.service;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.mahefa.common.enumerator.AnimationSpeed;
import org.mahefa.common.enumerator.ServiceState;
import org.mahefa.component.Grid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mahefa.common.CellStyle.Flag.NONE;

public class GridService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridService.class);

    protected Grid grid;
    private MazeService mazeService;
    private RouteFinderService routeFinderService;

    private BooleanProperty isReady = new SimpleBooleanProperty(false);

    public GridService(Grid grid) {
        this.grid = grid;
        this.mazeService = new MazeService(grid);
        this.routeFinderService = new RouteFinderService(grid);

        isReady.bind(
                Bindings.and(
                        mazeService.currentStateProperty().isEqualTo(ServiceState.IDLE),
                        routeFinderService.currentStateProperty().isEqualTo(ServiceState.IDLE)
                )
        );
    }

    public Grid getGrid() {
        return grid;
    }

    public MazeService getMazeService() {
        return mazeService;
    }

    public RouteFinderService getRouteFinderService() {
        return routeFinderService;
    }

    public boolean isReady() {
        return isReady.get();
    }

    public BooleanProperty isReadyProperty() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady.set(isReady);
    }

    public void setCurrentState(ServiceState currentState) {
        mazeService.setCurrentState(currentState);
        routeFinderService.setCurrentState(currentState);
    }

    public void updateSpeed(AnimationSpeed currentSpeed) {
        getMazeService().updateSpeed(currentSpeed);
        getRouteFinderService().updateSpeed(currentSpeed);
    }

    public void clearBoard() {
        clear(true);
        LOGGER.debug("Board cleared");
    }

    public void clearPath() {
        clear(false);
        LOGGER.debug("Path cleared");
    }

    private void clear(boolean reset) {
        mazeService.currentStateProperty().setValue(ServiceState.STOPPING);
        routeFinderService.currentStateProperty().setValue(ServiceState.STOPPING);

        if (grid != null) {
            grid.setDefaultFlag(NONE);
            grid.clear(reset, reset);
        }
    }
}

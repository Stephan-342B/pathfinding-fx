package org.mahefa.service;

import org.mahefa.common.enumerator.PathFindingAlgorithm;
import org.mahefa.common.enumerator.ServiceState;
import org.mahefa.common.exceptions.MissingAlgorithmException;
import org.mahefa.common.exceptions.UnsupportedAlgorithmException;
import org.mahefa.component.Grid;
import org.mahefa.service.pathfinding.AStar;
import org.mahefa.service.pathfinding.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RouteFinderService extends AnimatableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RouteFinderService.class);

    private Grid grid;
    private PathFindingAlgorithm algorithm;
    private Solver solver;

    public RouteFinderService(Grid grid) {
        init();
        this.grid = grid;
    }

    public void init() {
        currentSpeed.addListener((observableValue, animationSpeed, t1) -> {
            if (solver != null) {
                solver.setCurrentSpeed(t1.getInterval());
            }
        });
    }

    public Solver getSolver() {
        return solver;
    }

    public void setAlgorithm(PathFindingAlgorithm inputAlgorithm) {
        algorithm = inputAlgorithm;
    }

    public void run() {
        if (algorithm == null)
            throw new MissingAlgorithmException("Pick an Algorithm!");

        LOGGER.debug("Generating maze ==> {}", algorithm);

        // Force complete the previous one if still running
        currentStateProperty().setValue(ServiceState.STOPPING);

        // Select maze algorithm
        switch (algorithm) {
            case DIJKSTRA:
                break;
            case A_STAR:
                solver = new AStar(grid);
                break;
            case BREADTH_FIRST_SEARCH:
                break;
            case DEPTH_FIRST_SEARCH:
                break;
            default:
                throw new UnsupportedAlgorithmException("Unsupported algorithm: " + algorithm);
        }

        if (solver != null) {
            currentStateProperty().setValue(ServiceState.STARTING);
            grid.clear(false, false);
            solver.setCurrentSpeed(currentSpeed.get().getInterval());
            solver.isRunningProperty().addListener((observableValue, aBoolean, t1) -> {
                if (!t1) {
                    setCurrentState(ServiceState.COMPLETED);
                }
            });

            run(solver.solve());
        }
    }
}

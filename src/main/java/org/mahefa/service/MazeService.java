package org.mahefa.service;

import javafx.animation.AnimationTimer;
import org.mahefa.common.enumerator.MazeAlgorithm;
import org.mahefa.common.enumerator.ServiceState;
import org.mahefa.common.exceptions.UnsupportedAlgorithmException;
import org.mahefa.component.Grid;
import org.mahefa.service.maze_generator.AldousBroder;
import org.mahefa.service.maze_generator.MazeGenerator;
import org.mahefa.service.maze_generator.Randomized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mahefa.common.CellStyle.Flag.NONE;
import static org.mahefa.common.CellStyle.Flag.WALL_NODE;

public class MazeService extends AnimatableService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MazeService.class);

    private Grid grid;
    private MazeAlgorithm algorithm;
    private MazeGenerator mazeGenerator;

    public MazeService(Grid grid) {
        init();
        this.grid = grid;
    }

    public void init() {
        currentSpeed.addListener((observableValue, animationSpeed, t1) -> {
            if (mazeGenerator != null) {
                mazeGenerator.setCurrentSpeed(t1.getInterval());
            }
        });
    }

    public void setAlgorithm(MazeAlgorithm inputAlgorithm) {
        algorithm = inputAlgorithm;
    }

    public void run() {
        if (algorithm != null) {
            LOGGER.debug("Generating maze ==> {}", algorithm);

            // Force complete the previous one if still running
            setCurrentState(ServiceState.STOPPING);

            // Select maze algorithm
            switch (algorithm) {
                case ALDOUS_BRODER:
                    grid.setDefaultFlag(WALL_NODE);
                    mazeGenerator = new AldousBroder(grid);
                    mazeGenerator.setCurrentSpeed(currentSpeed.get().getInterval());
                    break;
                case BASIC_RANDOM:
                    grid.setDefaultFlag(NONE);
                    mazeGenerator = new Randomized(grid);
                    break;
                default:
                    throw new UnsupportedAlgorithmException("Unsupported algorithm: " + algorithm);
            }

            grid.clear(true, false);
            mazeGenerator.isRunningProperty().addListener((observableValue, aBoolean, t1) -> {
                if (!t1) {
                    setCurrentState(ServiceState.COMPLETED);
                }
            });

            AnimationTimer animationTimer = mazeGenerator.build();

            if (animationTimer != null)
                run(animationTimer);
        }
    }
}

package org.mahefa.service.concurrent;

import javafx.animation.AnimationTimer;
import org.mahefa.common.enumerator.MazeAlgorithm;
import org.mahefa.common.exceptions.UnsupportedAlgorithmException;
import org.mahefa.component.Grid;
import org.mahefa.service.maze_generator.AldousBroder;
import org.mahefa.service.maze_generator.MazeGenerator;
import org.mahefa.service.maze_generator.Randomized;

import java.util.function.Supplier;

import static org.mahefa.common.CellStyle.Flag.NONE;
import static org.mahefa.common.CellStyle.Flag.WALL_NODE;

/**
 * {@link javafx.concurrent.Worker}-based equivalent of {@link org.mahefa.service.MazeService},
 * reusing the existing {@link MazeGenerator} implementations unchanged.
 */
public class MazeGeneratorWorker extends AnimationWorker<MazeGenerator> {

    private final Grid grid;

    private MazeAlgorithm algorithm;
    private MazeGenerator mazeGenerator;

    public MazeGeneratorWorker(Grid grid) {
        this.grid = grid;
    }

    public void setAlgorithm(MazeAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public MazeGenerator getMazeGenerator() {
        return mazeGenerator;
    }

    @Override
    protected void onSpeedChanged(Long speed) {
        if (mazeGenerator != null && speed != null) {
            mazeGenerator.setCurrentSpeed(speed);
        }
    }

    public void start() {
        if (algorithm == null) {
            return;
        }

        if (isRunning()) {
            cancel();
        }

        switch (algorithm) {
            case ALDOUS_BRODER:
                grid.setDefaultFlag(WALL_NODE);
                mazeGenerator = new AldousBroder(grid);
                mazeGenerator.setCurrentSpeed(getCurrentSpeed());
                break;
            case BASIC_RANDOM:
                grid.setDefaultFlag(NONE);
                mazeGenerator = new Randomized(grid);
                break;
            default:
                throw new UnsupportedAlgorithmException("Unsupported algorithm: " + algorithm);
        }

        grid.clear(true, false, false);

        Supplier<AnimationTimer> animationTimerSupplier = mazeGenerator.build();

        if (animationTimerSupplier != null) {
            begin(animationTimerSupplier, mazeGenerator.isRunningProperty(), mazeGenerator);
        }
    }
}

package com.mahefa.pathfindingfx.service.concurrent;

import javafx.animation.AnimationTimer;
import com.mahefa.pathfindingfx.domain.enumerator.MazeAlgorithm;
import com.mahefa.pathfindingfx.exception.UnsupportedAlgorithmException;
import com.mahefa.pathfindingfx.ui.component.Grid;
import com.mahefa.pathfindingfx.service.concurrent.progress.Narrative;
import com.mahefa.pathfindingfx.service.concurrent.progress.ProgressReporter;
import com.mahefa.pathfindingfx.algorithm.maze.AldousBroder;
import com.mahefa.pathfindingfx.algorithm.maze.MazeGenerator;
import com.mahefa.pathfindingfx.algorithm.maze.Randomized;

import java.util.function.Supplier;

import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag.NONE;
import static com.mahefa.pathfindingfx.ui.style.CellStyle.Flag.WALL_NODE;

/**
 * {@link javafx.concurrent.Worker}-based equivalent of the former {@code MazeService},
 * reusing the existing {@link MazeGenerator} implementations unchanged.
 */
@Deprecated(forRemoval = true) // superseded by the Stepper/StepPlayer pipeline; kept until removal
public class MazeGeneratorWorker extends AnimationWorker<MazeGenerator> {

    private final Grid grid;
    private final ProgressReporter reporter = new ProgressReporter();

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

        String header = String.format("%s maze %s %s  %s  %d×%d grid",
                Narrative.START, Narrative.DOT, algorithm.getLabel(), Narrative.ARROW,
                grid.getRowLen(), grid.getColLen());
        updateMessage(header);

        Supplier<AnimationTimer> animationTimerSupplier = mazeGenerator.build();

        if (animationTimerSupplier != null) {
            reporter.start(header, null, "carving",
                    () -> String.format("%d/%d cells",
                            mazeGenerator.getVisitedCount(), mazeGenerator.getTotalCells()));
            begin(animationTimerSupplier, mazeGenerator.isRunningProperty(), mazeGenerator);
        } else {
            // Synchronous generator (Randomized) already finished inside build() — nothing to animate.
            String summary = summary(0);
            updateMessage(summary);
            reporter.report(header, summary);
        }
    }

    @Override
    protected void onSucceeded() {
        String summary = summary(reporter.elapsedSeconds());
        updateMessage(summary);
        reporter.finish(true, summary);
    }

    @Override
    protected void onCancelled() {
        if (!reporter.isReporting()) {
            return;
        }

        String summary = String.format("%s Cancelled %s %s",
                Narrative.FAIL, Narrative.DOT, Narrative.human(reporter.elapsedSeconds()));
        updateMessage(summary);
        reporter.finish(false, summary);
    }

    private String summary(long secs) {
        return String.format("%s Maze ready %s %d cells %s %s",
                Narrative.OK, Narrative.DOT, mazeGenerator.getTotalCells(), Narrative.DOT,
                Narrative.human(secs));
    }
}

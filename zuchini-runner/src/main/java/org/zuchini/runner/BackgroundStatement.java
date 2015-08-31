package org.zuchini.runner;

import org.zuchini.model.Background;

import java.util.List;

public class BackgroundStatement implements Statement {
    private final Background background;
    private final List<StepStatement> steps;

    public BackgroundStatement(Background background, List<StepStatement> steps) {
        this.background = background;
        this.steps = steps;
    }

    public Background getBackground() {
        return background;
    }

    public List<StepStatement> getSteps() {
        return steps;
    }

    @Override
    public void evaluate(Context context) throws Throwable {
        for (StepStatement step : steps) {
            step.evaluate(context);
        }
    }
}

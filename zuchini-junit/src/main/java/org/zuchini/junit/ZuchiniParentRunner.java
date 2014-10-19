package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sorter;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

abstract class ZuchiniParentRunner<T> extends ParentRunner<T> {

    protected ZuchiniParentRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    protected abstract String getName();
    protected abstract String getLocation();

    @Override
    public Description getDescription() {
        Description description =Description.createTestDescription(getLocation(), getName(), getRunnerAnnotations());
        for (T child : getChildren()) {
            description.addChild(describeChild(child));
        }
        return description;
    }

    @Override
    public void filter(Filter filter) throws NoTestsRemainException {
        throw new UnsupportedOperationException("Filtering is not supported");
    }

    @Override
    public void sort(Sorter sorter) {
        throw new UnsupportedOperationException("Sorting is not supported");
    }
}

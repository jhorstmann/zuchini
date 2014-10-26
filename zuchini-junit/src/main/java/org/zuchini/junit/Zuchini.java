package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.Scope;
import org.zuchini.runner.ThreadLocalScope;

import java.io.IOException;
import java.util.List;

public class Zuchini extends ParentRunner<FeatureRunner> {

    private final ZuchiniRunnerDelegate delegate;
    private final Scope globalScope;
    private final Scope scenarioScope;

    public Zuchini(Class<?> testClass) throws InitializationError, IOException, IllegalAccessException, InstantiationException {
        super(testClass);
        this.globalScope = new GlobalScope();
        this.scenarioScope = new ThreadLocalScope();
        ZuchiniOptions options = testClass.getAnnotation(ZuchiniOptions.class);
        this.delegate = new ZuchiniRunnerDelegate(testClass, options, scenarioScope);
    }

    @Override
    public void run(RunNotifier notifier) {
        globalScope.begin();
        try {
            super.run(notifier);
        } finally {
            globalScope.end();
        }
    }

    @Override
    protected List<FeatureRunner> getChildren() {
        return delegate.getChildren();
    }

    @Override
    protected Description describeChild(FeatureRunner child) {
        return delegate.describeChild(child);
    }

    @Override
    protected void runChild(FeatureRunner child, RunNotifier notifier) {
        delegate.runChild(child, notifier);
    }

}

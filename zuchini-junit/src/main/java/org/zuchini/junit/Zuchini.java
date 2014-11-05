package org.zuchini.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.Scope;
import org.zuchini.runner.ThreadLocalScope;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class Zuchini extends ParentRunner<ZuchiniRunnerDelegate> {

    private final ZuchiniRunnerDelegate delegate;
    private final Scope globalScope;
    private final Scope scenarioScope;

    public Zuchini(Class<?> testClass) throws InitializationError, IOException, IllegalAccessException, InstantiationException {
        super(testClass);
        this.globalScope = new GlobalScope();
        this.scenarioScope = new ThreadLocalScope();
        this.delegate = new ZuchiniRunnerDelegate(testClass, globalScope, scenarioScope);
    }

    @Override
    protected Statement withBeforeClasses(final Statement statement) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                globalScope.begin();
                statement.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfterClasses(final Statement statement) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } finally {
                    globalScope.end();
                }
            }
        };
    }

    @Override
    protected List<ZuchiniRunnerDelegate> getChildren() {
        return Collections.singletonList(delegate);
    }

    @Override
    protected Description describeChild(ZuchiniRunnerDelegate child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(ZuchiniRunnerDelegate child, RunNotifier notifier) {
        child.run(notifier);
    }

}

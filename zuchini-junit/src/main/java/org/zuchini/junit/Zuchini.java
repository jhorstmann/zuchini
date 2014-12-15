package org.zuchini.junit;

import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.zuchini.runner.GlobalScope;
import org.zuchini.runner.Scope;
import org.zuchini.runner.ThreadLocalScope;

import java.io.IOException;
import java.util.Collections;

public class Zuchini extends Suite {

    public Zuchini(Class<?> testClass) throws InitializationError, IOException, IllegalAccessException, InstantiationException {
        super(testClass, Collections.<Runner>singletonList(new ZuchiniRunnerDelegate(testClass, new GlobalScope(), new ThreadLocalScope())));
    }

    private Scope getGlobalScope() {
        ZuchiniRunnerDelegate delegate = (ZuchiniRunnerDelegate) getChildren().get(0);
        return delegate.getGlobalScope();
    }

    @Override
    protected Statement withBeforeClasses(final Statement statement) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                getGlobalScope().begin();
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
                    getGlobalScope().end();
                }
            }
        };
    }

}

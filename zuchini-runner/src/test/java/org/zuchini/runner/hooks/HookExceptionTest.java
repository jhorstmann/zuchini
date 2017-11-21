package org.zuchini.runner.hooks;

import cucumber.api.java.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zuchini.annotations.Before;
import org.zuchini.annotations.Given;
import org.zuchini.runner.Scope;
import org.zuchini.runner.World;
import org.zuchini.runner.WorldBuilder;

import java.io.IOException;

public class HookExceptionTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private boolean beforeHookCalled;
    private boolean afterHookCalled;
    private boolean stepExecuted;
    private boolean scopeBeginCalled;
    private boolean scopeEndCalled;
    private World world = buildWorld();
    private TestAction testAction;

    private World buildWorld() {
        try {
            return new WorldBuilder(HookExceptionTest.class.getClassLoader())
                    .addFeaturePackage("features/hooks")
                    .addStepDefinitionPackage("org.zuchini.runner.hooks")
                    .withScenarioScope(new Scope() {
                        @Override
                        public void begin() {
                            scopeBeginCalled = true;
                        }

                        @Override
                        public <T> T getObject(Class<T> clazz) {
                            return clazz == HookExceptionTest.class ? clazz.cast(HookExceptionTest.this) : null;
                        }

                        @Override
                        public void end() {
                            scopeEndCalled = true;
                        }
                    })
                    .buildWorld();
        } catch (IOException e) {
            throw new IllegalStateException("Could not build world", e);
        }
    }

    static class MyException extends RuntimeException {
        MyException() {
        }

        MyException(String message) {
            super(message);
        }
    }

    static enum TestAction {
        SUCCESS , EXCEPTION_STEP ,
        EXCEPTION_BEFORE, EXCEPTION_AFTER
    }

    @Before
    public void before() {
        beforeHookCalled = true;
        if (testAction == TestAction.EXCEPTION_BEFORE) {
            throw new MyException("Exception in before");
        }
    }

    @After
    public void after() {
        afterHookCalled = true;
        if (testAction == TestAction.EXCEPTION_AFTER) {
            throw new MyException("Exception in after");
        }
    }

    @Given("^a step is executed$")
    public void step() {
        stepExecuted = true;
        if (testAction == TestAction.EXCEPTION_STEP) {
            throw new MyException("Exception in step");
        }
    }

    @Test
    public void shouldCallHooks() throws Throwable {
        testAction = TestAction.SUCCESS;

        try {
            world.run();
        } finally {

            Assert.assertTrue("scope begin called", scopeBeginCalled);
            Assert.assertTrue("before called", beforeHookCalled);
            Assert.assertTrue("step called", stepExecuted);
            Assert.assertTrue("after called", afterHookCalled);
            Assert.assertTrue("scope end called", scopeEndCalled);
        }
    }

    @Test
    public void shouldCallAfterHooksOnException() throws Throwable {
        testAction = TestAction.EXCEPTION_STEP;
        expectedException.expect(MyException.class);
        expectedException.expectMessage("Exception in step");

        try {
            world.run();
        } finally {

            Assert.assertTrue("scope begin called", scopeBeginCalled);
            Assert.assertTrue("before called", beforeHookCalled);
            Assert.assertTrue("step called", stepExecuted);
            Assert.assertTrue("after called", afterHookCalled);
            Assert.assertTrue("scope end called", scopeEndCalled);
        }
    }

    @Test
    public void shouldCallAfterHooksOnExceptionInBeforeHooks() throws Throwable {
        testAction = TestAction.EXCEPTION_BEFORE;
        expectedException.expect(MyException.class);
        expectedException.expectMessage("Exception in before");

        try {
            world.run();
        } finally {

            Assert.assertTrue("scope begin called", scopeBeginCalled);
            Assert.assertTrue("before called", beforeHookCalled);
            Assert.assertFalse("step called", stepExecuted);
            Assert.assertTrue("after called", afterHookCalled);
            Assert.assertTrue("scope end called", scopeEndCalled);
        }
    }

    @Test
    public void shouldEndScopeOnExceptionInAfterHooks() throws Throwable {
        testAction = TestAction.EXCEPTION_AFTER;
        expectedException.expect(MyException.class);
        expectedException.expectMessage("Exception in after");

        try {
            world.run();
        } finally {

            Assert.assertTrue("scope begin called", scopeBeginCalled);
            Assert.assertTrue("before called", beforeHookCalled);
            Assert.assertTrue("step called", stepExecuted);
            Assert.assertTrue("after called", afterHookCalled);
            Assert.assertTrue("scope end called", scopeEndCalled);
        }
    }

}

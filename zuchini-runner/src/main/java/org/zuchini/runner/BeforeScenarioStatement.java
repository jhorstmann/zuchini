package org.zuchini.runner;

import java.util.List;

class BeforeScenarioStatement implements Statement {
    private final List<HookStatement> hooks;

    BeforeScenarioStatement(List<HookStatement> hooks) {
        this.hooks = hooks;
    }

    @Override
    public void evaluate(Context context) throws Throwable {
        context.getScenarioScope().begin();
        for (HookStatement hook : hooks) {
            hook.evaluate(context);
        }
    }
}

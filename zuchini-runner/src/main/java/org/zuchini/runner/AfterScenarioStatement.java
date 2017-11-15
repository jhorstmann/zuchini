package org.zuchini.runner;

import java.util.List;

class AfterScenarioStatement implements Statement {
    private final List<HookStatement> hooks;

    AfterScenarioStatement(List<HookStatement> hooks) {
        this.hooks = hooks;
    }

    @Override
    public void evaluate(Context context) throws Throwable {
        try {
            for (HookStatement hook : hooks) {
                hook.evaluate(context);
            }
        } finally {
            context.getScenarioScope().end();
        }
    }
}

package org.zuchini.examples.datatables;

import org.zuchini.annotations.Before;
import org.zuchini.annotations.Given;
import org.zuchini.annotations.Then;
import org.zuchini.runner.tables.Datatable;
import org.zuchini.runner.tables.NamingConvention;
import org.zuchini.runner.tables.NamingConventions.DefaultNamingConventions;

import java.lang.RuntimeException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.zuchini.runner.tables.DatatableMatchers.matchesTable;

public class HookSteps {
    private final Set<String> executedHooks = new TreeSet<>();

    @Before()
    public void untagged1() {
        executedHooks.add("untagged1");
    }

    @Before()
    public void untagged2() {
        executedHooks.add("untagged2");
    }

    @Before("tag1")
    public void tag1() {
        executedHooks.add("tag1");
    }

    @Given("^a step$")
    public void a_step() {
    }

    @Then("^the executed hooks are:")
    public void the_executed_hooks_are(Datatable expected) {

        final List<List<String>> rows = new ArrayList<>(executedHooks.size());
        for (String hook : executedHooks) {
            rows.add(Collections.singletonList(hook));
        }

        final Datatable actual = Datatable.fromLists(rows);

        assertThat(actual, matchesTable(expected));
    }

}

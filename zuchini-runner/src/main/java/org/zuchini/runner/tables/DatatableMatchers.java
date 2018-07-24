package org.zuchini.runner.tables;

import org.hamcrest.Matcher;

public class DatatableMatchers {

    private DatatableMatchers() {

    }

    public static Matcher<Datatable> matchesTable(Datatable expected) {
        return new DatatableMatcher(expected);
    }
}

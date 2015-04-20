package org.zuchini.runner.tables;

public class DatatableMatchers {
    private DatatableMatchers() {

    }

    public static DatatableMatcher matchesTable(Datatable expected) {
        return new DatatableMatcher(expected);
    }
}

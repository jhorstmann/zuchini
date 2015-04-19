package org.zuchini.examples.datatables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Diff using longest common subsequence as described in
 * <a href="http://introcs.cs.princeton.edu/java/96optimization/">
 * Introduction to Programming in Java - Dynamic Programming</a>.
 */
class Diff {
    private Diff() {

    }

    static <T> String[] formatDiff(T[] x, T[] y) {

        int M = x.length;
        int N = y.length;

        // opt[i][j] = length of LCS of x[i..M] and y[j..N]
        int[][] opt = new int[M + 1][N + 1];

        // compute length of LCS and all subproblems via dynamic programming
        for (int i = M - 1; i >= 0; i--) {
            for (int j = N - 1; j >= 0; j--) {
                if (x[i].equals(y[j]))
                    opt[i][j] = opt[i + 1][j + 1] + 1;
                else
                    opt[i][j] = Math.max(opt[i + 1][j], opt[i][j + 1]);
            }
        }

        // conservative guess for size of diff
        List<String> diff = new ArrayList<>(M + N);

        // recover LCS itself and print out non-matching lines to standard output
        int i = 0, j = 0;
        while (i < M && j < N) {
            if (x[i].equals(y[j])) {
                diff.add(" " + x[i]);
                i++;
                j++;
            } else if (opt[i + 1][j] >= opt[i][j + 1]) {
                diff.add("-" + x[i++]);
            } else {
                diff.add("+" + y[j++]);
            }
        }

        // dump out one remainder of one string if the other is exhausted
        while (i < M || j < N) {
            if (i == M) {
                diff.add("+" + y[j++]);
            } else if (j == N) {
                diff.add("-" + x[i++]);
            }
        }

        return diff.toArray(new String[diff.size()]);
    }
}

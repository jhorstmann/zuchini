package org.zuchini.parser;

import org.antlr.v4.runtime.tree.RuleNode;
import org.zuchini.gherkin.antlr.GherkinBaseVisitor;
import org.zuchini.gherkin.antlr.GherkinParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ParserHelper {
    private ParserHelper() {
    }

    static String trimCell(GherkinParser.CellContext cell) {
        return cell.getText().replaceAll("^[\t ]+|[\t ]+$", "").replace("\\|", "|");
    }

    static <C, T extends RuleNode, V extends GherkinBaseVisitor<C>> List<C> visitNodes(List<T> ruleNodes, V visitor) {
        List<C> result = new ArrayList<>(ruleNodes.size());
        for (T node : ruleNodes) {
            result.add(node.accept(visitor));
        }
        return result;
    }

    static <C, T extends RuleNode, V extends Visitors.AggregatingVisitor<C>> List<C> visitNodesAndAggregate(List<T> ruleNodes, V visitor) {
        List<C> result = new ArrayList<>(ruleNodes.size());
        for (T node : ruleNodes) {
            result.addAll(node.accept(visitor));
        }
        return result;
    }

    static <C, T extends RuleNode, V extends GherkinBaseVisitor<C>> List<C> visitOptionalNode(T node, V visitor) {
        if (node == null) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(node.accept(visitor));
        }
    }
}

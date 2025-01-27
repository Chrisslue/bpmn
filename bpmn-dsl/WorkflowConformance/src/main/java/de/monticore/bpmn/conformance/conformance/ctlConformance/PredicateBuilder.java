package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateBuilder {

  public static WfPredicate postPredicate(WfNode node) {
    if (node.getSuccessors().isEmpty()) {
      return new WfPredicate(n -> true, "true");
    }

    if (node.getSuccessors().size() == 1) {
      return postPredicateRecursive(node.getSuccessors().iterator().next());
    }

    if (node.getNodeType().isGateway()) {
      Log.error(String.format("cannot compute post predicate of %s", node));
      assert false;
    }

    Log.error(
        String.format(
            "Trying to compute post predicate of a node %s having more than one successors", node));
    assert false;
    return null;
  }

  public static WfPredicate prePredicate(WfNode node) {
    if (node.getPredecessors().isEmpty()) {
      return new WfPredicate(n -> true, "true");
    }

    if (node.getPredecessors().size() == 1) {
      return prePredicateRecursive(node.getPredecessors().iterator().next());
    }

    if (node.getNodeType().isGateway()) {
      Log.error(String.format("cannot compute post predicate of %s", node));
      assert false;
    }

    Log.error(
        String.format(
            "Trying to compute post predicate of a node %s having more than one successors", node));
    assert false;
    return null;
  }

  private static WfPredicate prePredicateRecursive(WfNode node) {

    List<WfPredicate> prevprev = new ArrayList<>();
    if (node.getNodeType().isGateway()) {
      prevprev =
          node.getPredecessors().stream()
              .map(PredicateBuilder::prePredicateRecursive)
              .collect(Collectors.toList());
    }

    switch (node.getNodeType()) {
      case TASK:
      case EVENT:
        return mkVar(node);
      case XOR_MERGE:
        case OR_MERGE:
            return mkOr(prevprev);
        case AND_MERGE:
        return mkAnd(prevprev);
      default:
        assert prevprev.size() == 1;
        return prevprev.iterator().next();
    }
  }

  private static WfPredicate postPredicateRecursive(WfNode node) {

    List<WfPredicate> sucsuc = new ArrayList<>();
    if (node.getNodeType().isGateway()) {
      sucsuc =
          node.getSuccessors().stream()
              .map(PredicateBuilder::postPredicateRecursive)
              .collect(Collectors.toList());
    }

    switch (node.getNodeType()) {
      case TASK:
      case EVENT:
        return mkVar(node);
      case XOR_SPLIT:
        return mkXor(sucsuc);
      case OR_SPLIT:
        return mkOr(sucsuc);
      case AND_SPLIT:
        return mkAnd(sucsuc);
      default:
        assert sucsuc.size() == 1;
        return sucsuc.iterator().next();
    }
  }

  public static WfPredicate mkVar(WfNode left) {
    Predicate<List<WfNode>> pred = confWfNodes -> confWfNodes.contains(left);
    return new WfPredicate(pred, left + "");
  }

  public static WfPredicate mkAnd(List<WfPredicate> formulas) {
    Predicate<List<WfNode>> res = set -> true;
    String predicateString = "true";
    for (var formula : formulas) {
      res = res.and(formula.getPredicate());
      predicateString = "(" + predicateString.concat(" and " + formula) + ")";
    }
    return new WfPredicate(res, predicateString);
  }

  public static WfPredicate mkOr(List<WfPredicate> formulas) {
    Predicate<List<WfNode>> res = set -> false;
    String predicateString = "false";
    for (var formula : formulas) {
      res = res.or(formula.getPredicate());
      predicateString = predicateString.concat(" or " + formula);
    }
    return new WfPredicate(res, predicateString);
  }

  public static WfPredicate mkXor(List<WfPredicate> formulas) {
    if (formulas == null || formulas.size() < 2) {
      Log.error("Xor requires at least 2 formulas.");
      assert false;
    }

    // Combine predicates into a single predicate using a lambda
    Predicate<List<WfNode>> combinedPredicate =
        nodes -> formulas.stream().filter(f -> f.getPredicate().test(nodes)).count() == 1;

    // Combine descriptions using Stream API
    String predicateString =
        formulas.stream().map(WfPredicate::toString).collect(Collectors.joining(" xor "));

    return new WfPredicate(combinedPredicate, predicateString);
  }
}

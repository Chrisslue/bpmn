package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateGenerator {

  public static Predicate<List<WfNode>> postPredicate(WfNode node) {
    assert node.getSuccessors().size() == 1;
    return postPredicateRecursive(node.getSuccessors().iterator().next());
  }

  private static Predicate<List<WfNode>> postPredicateRecursive(WfNode node) {

    List<Predicate<List<WfNode>>> sucsuc =
        node.getSuccessors().stream()
            .map(PredicateGenerator::postPredicateRecursive)
            .collect(Collectors.toList());

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
        Log.error("Not implemented yet");
        assert false;
    }
    Log.error("something wrong happened");
    assert false;
    return null;
  }

  public static Predicate<List<WfNode>> mkVar(WfNode left) {
    System.out.print(left);
    return confWfNodes -> confWfNodes.contains(left);
  }

  public static Predicate<List<WfNode>> mkAnd(List<Predicate<List<WfNode>>> formulas) {
    System.out.print("and");
    Predicate<List<WfNode>> res = set -> true;
    for (var formula : formulas) {
      res = res.and(formula);
    }
    return res;
  }

  public static Predicate<List<WfNode>> mkOr(List<Predicate<List<WfNode>>> formulas) {
    System.out.print("or");
    Predicate<List<WfNode>> res = set -> false;
    for (var formula : formulas) {
      res = res.or(formula);
    }
    return res;
  }

  public static Predicate<List<WfNode>> mkXor(List<Predicate<List<WfNode>>> formulas) {
    System.out.print("xor");

    if (formulas.size() < 2) {
      Log.error("Xor need at least 2 formulas");
    }

    List<Predicate<List<WfNode>>> formulaList = new ArrayList<>(formulas);
    Predicate<List<WfNode>> res = formulaList.get(0);

    for (int i = 1; i < formulaList.size(); i++) {
      Predicate<List<WfNode>> finalRes = res;
      int finalI = i;
      res = set -> finalRes.test(set) ^ formulaList.get(finalI).test(set);
    }
    return res;
  }
}

package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.se_rwth.commons.logging.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateGenerator {

  public static Predicate<Set<IDWfNode>> postPredicate(IDWfNode node) {

    Predicate<Set<IDWfNode>> subPred;
    Set<Predicate<Set<IDWfNode>>> sucSuc =
        node.getSuccessors().stream()
            .map(PredicateGenerator::postPredicate)
            .collect(Collectors.toSet());
    switch (node.getNodeType()) {
      case XOR_SPLIT:
        subPred = mkXor(sucSuc);
        break;
      case OR_SPLIT:
        subPred = mkOr(sucSuc);
        break;
      default:
        subPred = mkAnd(sucSuc);
    }

    if (node.isGateway()) {
      return subPred;
    } else {
      return mkAnd(Set.of(mkVar(node), subPred));
    }
  }

  public static Predicate<Set<IDWfNode>> mkVar(IDWfNode left) {
    return confWfNodes -> confWfNodes.contains(left);
  }

  public static Predicate<Set<IDWfNode>> mkAnd(Set<Predicate<Set<IDWfNode>>> formulas) {

    Predicate<Set<IDWfNode>> res = set -> true;
    for (var formula : formulas) {
      res = res.and(formula);
    }
    return res;
  }

  public static Predicate<Set<IDWfNode>> mkOr(Set<Predicate<Set<IDWfNode>>> formulas) {

    Predicate<Set<IDWfNode>> res = set -> false;
    for (var formula : formulas) {
      res = res.or(formula);
    }
    return res;
  }

  public static Predicate<Set<IDWfNode>> mkXor(Set<Predicate<Set<IDWfNode>>> formulas) {
    if (formulas.size() < 2) {
      Log.error("Xor need at least 2 formulas");
    }

    List<Predicate<Set<IDWfNode>>> formulaList = new ArrayList<>(formulas);
    Predicate<Set<IDWfNode>> res = formulaList.get(0);

    for (int i = 1; i < formulaList.size(); i++) {
      Predicate<Set<IDWfNode>> finalRes = res;
      int finalI = i;
      res = set -> finalRes.test(set) ^ formulaList.get(finalI).test(set);
    }
    return res;
  }
}

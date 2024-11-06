package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateGenerator {
  public Predicate<Set<IdWfNode>> postPredicate(IdWfNode node) {

    Predicate<Set<IdWfNode>> subPred  =p->true;

    for (var suc : node.getSuccessors()) {

      if (suc.isGateway()) {
        Set<Predicate<Set<IdWfNode>>> sucSuc =
            suc.getSuccessors().stream().map(this::postPredicate).collect(Collectors.toSet());
        switch (suc.getNodeType()) {
          case XOR_SPLIT:
            subPred =(mkXor(sucSuc));
            break;
          case AND_SPLIT:
            subPred =mkAnd(sucSuc);
            break;
          case OR_SPLIT:
            subPred =mkOr(sucSuc);
            break;
          default:
            subPred =mkAnd(sucSuc);
        }
      } else {
        subPred =postPredicate(suc);
      }
    }

    return mkAnd(Set.of(mkVar(node), subPred));
  }

  public static Predicate<Set<IdWfNode>> mkVar(IdWfNode left) {
    return confWfNodes -> confWfNodes.contains(left);
  }

  public static Predicate<Set<IdWfNode>> mkAnd(Set<Predicate<Set<IdWfNode>>> formulas) {

    Predicate<Set<IdWfNode>> res = set -> true;
    for (var formula : formulas) {
      res = res.and(formula);
    }
    return res;
  }

  public static Predicate<Set<IdWfNode>> mkOr(Set<Predicate<Set<IdWfNode>>> formulas) {

    Predicate<Set<IdWfNode>> res = set -> true;
    for (var formula : formulas) {
      res = res.or(formula);
    }
    return res;
  }

  public static Predicate<Set<IdWfNode>> mkXor(Set<Predicate<Set<IdWfNode>>> formulas) {

    Predicate<Set<IdWfNode>> res = set -> true;
    for (Predicate<Set<IdWfNode>> formula : formulas) {
      Predicate<Set<IdWfNode>> finalRes = res;
      res = set -> finalRes.test(set) ^ formula.test(set);
    }
    return res;
  }
}

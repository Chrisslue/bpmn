package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import java.util.Set;
import java.util.function.Predicate;

public class PredicateUtils {

  public static Predicate<Set<IdWfNode>> mkVar(IdWfNode left) {
    return confWfNodes -> confWfNodes.contains(left);
  }

  public static Predicate<Set<IdWfNode>> mkAnd(
      Set<Predicate<Set<IdWfNode>>> formulas) {

    Predicate<Set<IdWfNode>> res = set -> true;
    for (var formula : formulas) {
      res = res.and(formula);
    }
    return res;
  }

  public static Predicate<Set<IdWfNode>> mkOr(
      Set<Predicate<Set<IdWfNode>>> formulas) {

    Predicate<Set<IdWfNode>> res = set -> true;
    for (var formula : formulas) {
      res = res.or(formula);
    }
    return res;
  }

  public static Predicate<Set<IdWfNode>> mkXor(
      Set<Predicate<Set<IdWfNode>>> formulas) {

    Predicate<Set<IdWfNode>> res = set -> true;
    for (Predicate<Set<IdWfNode>> formula : formulas) {
      Predicate<Set<IdWfNode>> finalRes = res;
      res = set -> finalRes.test(set) ^ formula.test(set);
    }
    return res;
  }

}

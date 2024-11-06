package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PredicateGenerator {
   private Predicate<Set<IdWfNode>> postPredicate(IdWfNode node){
       return PredicateUtils.mkAnd(node.getSuccessors().stream().map(this::postPredicate).collect(Collectors.toSet()));
   }
}

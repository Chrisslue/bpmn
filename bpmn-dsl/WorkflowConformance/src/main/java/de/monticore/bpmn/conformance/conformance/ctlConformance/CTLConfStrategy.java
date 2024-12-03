package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.conformance.ConformanceStrategy;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CTLConfStrategy implements ConformanceStrategy<WfNode> {

  protected WfBuilder ref;
  protected WfBuilder con;

  protected IncarnationStrategy<WfNode> incStrategy;

  public CTLConfStrategy(WfBuilder con, WfBuilder ref, IncarnationStrategy<WfNode> incStrategy) {
    this.con = con;
    this.ref = ref;
    this.incStrategy = incStrategy;
  }

  @Override
  public CheckResult checkConformance(WfNode concrete) {
    List<WfNode> references = incStrategy.getReferenceElements(concrete);

    if (references.isEmpty()) {
      return CheckResult.mkConform(concrete);
    }

    if (references.size() > 1) {
      Log.error("Found more than one reference to the concrete element  " + concrete);
      assert false;
    }

    Set<WfNode> startNodes =
        con.getAllNodes().stream().filter(WfNode::isStart).collect(Collectors.toSet());

    Log.println("");
    Log.info(String.format("Checking Conformance of %s to %s", concrete, references.get(0)), "");

    // building pre- and post-predicates
    Predicate<List<WfNode>> postPredicate = PredicateBuilder.postPredicate(references.get(0));
    Predicate<List<WfNode>> prePredicate = PredicateBuilder.prePredicate(references.get(0));

    // building a pre- and post-conformance visitor
    ConfWfVisitor postVisitor =
        new ConfWfVisitor(concrete, startNodes, postPredicate, incStrategy, false);
    ConfWfVisitor preVisitor =
        new ConfWfVisitor(concrete, startNodes, prePredicate, incStrategy, true);

    // traversing the concrete model for node
    ConfWfTraverser traverser = new ConfWfTraverser();
    traverser.traverseForward(postVisitor, null, concrete);
    preVisitor.setBackward();
    traverser.traverseBackward(preVisitor, null, concrete);


    Log.info(String.format("--- Result for forward traversing of Node %s --- ", concrete), "");
    postVisitor.printResult();

    Log.info(String.format("--- Result for backward traversing of Node %s --- ", concrete), "");
    preVisitor.printResult();
     var postResult = postVisitor.getResult();
     var preResult = preVisitor.getResult();

     if (postResult.isNonConform()) {
      return postResult;
    } else if (preResult.isNonConform()) {
      return preVisitor.getResult();
    } else if (postVisitor.getResult().isUnknown()) {
      return postVisitor.getResult();
    } else {
      return CheckResult.mkConform(concrete);
    }
  }
}

package de.monticore.bpmn.conformance.conformance.ctlConformance;

import de.monticore.bpmn.conformance.conformance.ConformanceStrategy;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CTLConfStrategy implements ConformanceStrategy<WfNode> {

  protected WfBuilder reference;
  protected WfBuilder concrete;
  protected IncarnationStrategy<WfNode> incarnationStrategy;

  public CTLConfStrategy(
      WfBuilder concrete, WfBuilder reference, IncarnationStrategy<WfNode> incarnationStrategy) {
    this.concrete = concrete;
    this.reference = reference;
    this.incarnationStrategy = incarnationStrategy;
  }

  @Override
  public CheckResult checkConformance(WfNode concrete) {

    List<WfNode> references = incarnationStrategy.getReferenceElements(concrete);

    // the node always conforms in the case it is no incarnated
    if (references.isEmpty()) {
      return CheckResult.mkConform(concrete);
    }

    // incarnation of multiple elements not yet implemented
    if (references.size() > 1) {
      Log.error("Found more than one reference to the concrete element  " + concrete);
      assert false;
    }

    Set<WfNode> startNodes =
        this.concrete.getAllNodes().stream().filter(WfNode::isStart).collect(Collectors.toSet());

    Log.trace("", "");
    Log.trace(
        String.format("Checking Conformance of [%s] to [%s]", concrete, references.get(0)), "");

    // building pre- and post-predicates
    WfPredicate postPredicate = PredicateBuilder.postPredicate(references.get(0));
    WfPredicate prePredicate = PredicateBuilder.prePredicate(references.get(0));

    // building a pre- and post-conformance visitor
    BranchVisitor forwardVisitor =
        BranchVisitor.mkForwardVisitor(concrete, startNodes, postPredicate, incarnationStrategy);
    BranchVisitor backwardVisitor =
        BranchVisitor.mkBackwardVisitor(concrete, startNodes, prePredicate, incarnationStrategy);

    // traversing the concrete model for node
    BFSConfWfTraverser fwdTraverser = new BFSConfWfTraverser(forwardVisitor, concrete);
    while (fwdTraverser.stepForward())
      ;

    BFSConfWfTraverser bwdTraverser = new BFSConfWfTraverser(backwardVisitor, concrete);
    while (bwdTraverser.stepBackward())
      ;

    Log.trace(String.format("--- Result for forward traversing of Node %s --- ", concrete), "");
    forwardVisitor.printResult();

    Log.trace(String.format("--- Result for backward traversing of Node %s --- ", concrete), "");
    backwardVisitor.printResult();

    var postResult = forwardVisitor.getResult();
    var preResult = backwardVisitor.getResult();

    // build the result by combining pre and post
    if (postResult.isNonConform()) {
      return postResult;
    } else if (preResult.isNonConform()) {
      return preResult;
    } else if (postResult.isUnknown()) {
      return postResult;
    } else {
      return CheckResult.mkConform(concrete);
    }
  }
}

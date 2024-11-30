package de.monticore.bpmn.conformance.conformance.ctlConformance;


import de.monticore.bpmn.conformance.conformance.ConformanceStrategy;
import de.monticore.bpmn.conformance.datastructures.utils.CheckResult;
import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import de.monticore.bpmn.conformance.incarnation.IncarnationStrategy;
import de.se_rwth.commons.logging.Log;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CTLConfStrategy implements ConformanceStrategy<WfNode> {
 public final String logger = "";
  protected WfBuilder ref;
  protected WfBuilder con;


  protected IncarnationStrategy<WfNode> incStrategy;

  public CTLConfStrategy(    WfBuilder con, WfBuilder  ref,  IncarnationStrategy<WfNode> incStrategy) {
  this.con = con;
  this.ref = ref;
  this.incStrategy = incStrategy ;
  }

  @Override
  public CheckResult checkConformance(WfNode concrete) {
    List<WfNode> references = incStrategy.getReferenceElements(concrete);

    if(references.size() > 1){
        Log.error("Found more than one reference to the concrete element  " + concrete);
        assert false;
    }


    if (references.isEmpty()) {
     return  CheckResult.mkConform(concrete);
    }

    Set<WfNode> startNodes = con.getAllNodes().stream().filter(WfNode::isStart).collect(Collectors.toSet());
    WfNode reference = references.get(0);

    Log.println("");
    Log.info(String.format("Checking Conformance of %s to %s", concrete, reference), logger);

    Predicate<List<WfNode>> refPredicate = PredicateGenerator.postPredicate(reference);

    ConfWfVisitor visitor = new ConfWfVisitor(concrete, startNodes, refPredicate,  incStrategy);

    ConfWfTraverser traverser = new ConfWfTraverser();
    traverser.traverseForward(visitor, null, concrete);

    visitor.printResult();

    return visitor.getResult();

  }



}

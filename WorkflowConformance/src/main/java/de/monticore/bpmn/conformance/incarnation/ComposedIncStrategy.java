/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance.incarnation;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.ArrayList;
import java.util.List;

public class ComposedIncStrategy implements IncarnationStrategy<WfNode> {
  
  protected WfBuilder refWorkflow;
  protected String mapping;
  
  List<IncarnationStrategy<WfNode>> incStrategies = new ArrayList<>();
  
  public ComposedIncStrategy(WfBuilder refWorkflow, String mapping) {
    this.refWorkflow = refWorkflow;
    this.mapping = mapping;
  }
  
  public void addIncStrategy(IncarnationStrategy<WfNode> strategy) {
    incStrategies.add(strategy);
  }
  
  public List<WfNode> getReferenceElements(WfNode concrete) {
    List<WfNode> refElements = new ArrayList<>();
    
    for (IncarnationStrategy<WfNode> strategy : incStrategies) {
      refElements.addAll(strategy.getReferenceElements(concrete));
      if (!refElements.isEmpty()) {
        return refElements;
      }
    }
    return refElements;
  }
  
  @Override
  public boolean isIncarnation(WfNode concrete, WfNode ref) {
    return getReferenceElements(concrete).contains(ref);
  }
  
}

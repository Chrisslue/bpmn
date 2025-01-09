package de.monticore.bpmn.conformance.incarnation;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.ArrayList;
import java.util.List;

public class StereotypesIncStrategy implements IncarnationStrategy<WfNode> {

  protected WfBuilder wfBuilder;
  protected String mapping;

  public StereotypesIncStrategy(WfBuilder wfBuilder, String mapping) {
    this.wfBuilder = wfBuilder;
    this.mapping = mapping;
  }

  @Override
  public List<WfNode> getReferenceElements(WfNode concrete) {
    List<WfNode> refTypes = new ArrayList<>();
    if (concrete.getStereotype().isPresent() && concrete.getStereotype().get().contains(mapping)) {

      String refName = concrete.getStereotype().get().getValue(mapping);
      refTypes.add(wfBuilder.getWfNode(refName));
    }
    return refTypes;
  }

  @Override
  public boolean isIncarnation(WfNode concrete, WfNode ref) {
    if (concrete.getStereotype().isPresent() && concrete.getStereotype().get().contains(mapping)) {

      String refName = concrete.getStereotype().get().getValue(mapping);
      return refName.equals(ref.getLabel());
    }
    return false;
  }
}

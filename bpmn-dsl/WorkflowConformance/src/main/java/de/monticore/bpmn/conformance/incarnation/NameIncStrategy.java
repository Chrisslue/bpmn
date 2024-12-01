package de.monticore.bpmn.conformance.incarnation;

import de.monticore.bpmn.conformance.datastructures.interf.WfBuilder;
import de.monticore.bpmn.conformance.datastructures.interf.WfNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NameIncStrategy implements IncarnationStrategy<WfNode> {
  private final WfBuilder reference;

  public NameIncStrategy(WfBuilder reference) {
    this.reference = reference;
  }

  @Override
  public List<WfNode> getReferenceElements(WfNode con) {
    Optional<? extends WfNode> ref =
        reference.getAllNodes().stream().filter(n -> n.getLabel().equals(con.getLabel())).findAny();

    return ref.<List<WfNode>>map(idWfNode -> new ArrayList<>(List.of(idWfNode)))
        .orElseGet(ArrayList::new);
  }

  @Override
  public boolean isIncarnation(WfNode srcElem, WfNode tgtElem) {
    return false;
  }
}

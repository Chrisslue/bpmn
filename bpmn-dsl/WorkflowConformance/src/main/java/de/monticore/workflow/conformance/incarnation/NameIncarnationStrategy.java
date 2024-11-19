package de.monticore.workflow.conformance.incarnation;

import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.datastructure.interf.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class NameIncarnationStrategy implements IncarnationStrategy {
  private final IDWfNodeBuilder reference;
  private final IDWfNodeBuilder concrete;

  public NameIncarnationStrategy(IDWfNodeBuilder reference, IDWfNodeBuilder concrete) {
    this.reference = reference;
    this.concrete = concrete;
  }

  @Override
  public List<WfNode> getReferenceElements(WfNode con) {
    Optional<IDWfNode> ref =
        reference.getAllNodes().stream().filter(n -> n.getLabel().equals(con.getLabel())).findAny();
    return ref.<List<WfNode>>map(idWfNode -> new ArrayList<>(List.of(idWfNode)))
        .orElseGet(ArrayList::new);
  }

  @Override
  public boolean isIncarnation(WfNode srcElem, WfNode tgtElem) {
    return false;
  }
}

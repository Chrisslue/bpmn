package de.monticore.workflow.conformance.incarnation;

import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.datastructure.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.Optional;

public class NameIncarnationStrategy implements IncarnationStrategy {
  private final IDWfNodeBuilder reference;
  private final IDWfNodeBuilder concrete;

  public NameIncarnationStrategy(IDWfNodeBuilder reference, IDWfNodeBuilder concrete) {
    this.reference = reference;
    this.concrete = concrete;
  }

  @Override
  public Optional<IDWfNode> getReferenceElements(WfNode con) {
    return reference.getNode(con.getLabel());
  }

  @Override
  public boolean isIncarnation(WfNode srcElem, WfNode tgtElem) {
    return false;
  }
}

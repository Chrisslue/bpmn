package de.monticore.workflow.conformance.incarnation;

import de.monticore.workflow.conformance.datastructure.IDWfNode;
import de.monticore.workflow.conformance.datastructure.IDWfNodeBuilder;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.Optional;

public class NameIncarnationStrategy {
  private final IDWfNodeBuilder reference;
  private final IDWfNodeBuilder concrete;

  public NameIncarnationStrategy(IDWfNodeBuilder reference, IDWfNodeBuilder concrete) {
    this.reference = reference;
    this.concrete = concrete;
  }

  public Optional<IDWfNode> getReference(WfNode con) {
    return reference.getNode(con.getLabel());
  }
}

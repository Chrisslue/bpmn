package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import java.util.Collections;
import java.util.Set;
// todo add a kinds of active nodes

public class TokenController {
  private final Set<IdWfNode> labels;
  private final Set<IdWfNode> activeNodes;

  public TokenController(Set<IdWfNode> labels,Set<IdWfNode> activeNodes) {
    this.labels = Collections.unmodifiableSet(labels);
    this.activeNodes = activeNodes;
  }

  public Set<IdWfNode> getActiveNodes() {
    return activeNodes;
  }

  public Set<IdWfNode> getLabels() {
    return labels;
  }

  @Override
  public String toString() {
    return "[" + labels.toString() + "]";
  }
}

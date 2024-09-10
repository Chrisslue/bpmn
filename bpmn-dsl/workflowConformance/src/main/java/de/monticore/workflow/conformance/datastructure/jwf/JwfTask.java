package de.monticore.workflow.conformance.datastructure.jwf;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.datastructure.interf.WfNodeType;

public class JwfTask implements WfNode {
  private String name;

  public JwfTask(String name) {
    this.name = name;
  }

  @Override
  public WfNodeType getNodeType() {
    return WfNodeType.TASK;
  }

  @Override
  public String getLabel() {
    return name;
  }

  @Override
  public String toString() {
    return getLabel();
  }
}

package de.monticore.workflow.conformance.datastructure.jwf;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.datastructure.interf.WfNodeType;

public class JwfGateway implements WfNode {

  private WfNodeType type;
  private String label;

  public JwfGateway(WfNodeType type,String label) {
    this.type = type;
    this.label = label;
  }

  @Override
  public WfNodeType getNodeType() {
    return type;
  }

  @Override
  public String getLabel() {
    return  label ;
  }

  @Override
  public String toString() {
    return "Xor:"+getLabel(); //todo fix implementation
  }
}

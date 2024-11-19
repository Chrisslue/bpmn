package de.monticore.workflow.conformance.datastructure.interf;

import de.monticore.workflow.conformance.utils.NodeType;
import java.util.Set;

/***
 * this class  represent a BPMNode
 * */
public interface WfNode {

  String getLabel();

  public NodeType getNodeType();

  //  Set<? extends WfNode> getSuccessors(int depth);
  //
  //  Set<? extends WfNode> getPredecessors(int depth);

  Set<? extends WfNode> getSuccessorsOfDepth(int depth);

  Set<? extends WfNode> getPredecessorsOfDepth(int depth);

  Set<? extends WfNode> getSuccessors();

  Set<? extends WfNode> getPredecessors();

  boolean isGateway();
}

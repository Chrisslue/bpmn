package de.monticore.bpmn.conformance.datastructures.interf;

import de.monticore.bpmn.conformance.datastructures.utils.NodeType;
import de.monticore.umlstereotype._ast.ASTStereotype;
import java.util.Optional;
import java.util.Set;

/***
 * this class  represent a BPMNode
 * */
public interface WfNode {

  Optional<ASTStereotype> getStereotype();

  String getLabel();

  NodeType getNodeType();

  boolean isStart();

  boolean isEnd();

  Set<? extends WfNode> getSuccessors();

  Set<? extends WfNode> getPredecessors();
}

package de.monticore.workflow.conformance.datastructure.interf;

/***
 * this class  represent a BPMNode
 * */
public interface WfNode {
  WfNodeType getNodeType();
  String getLabel();
}

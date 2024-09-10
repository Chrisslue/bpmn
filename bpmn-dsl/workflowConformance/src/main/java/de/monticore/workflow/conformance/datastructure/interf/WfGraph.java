package de.monticore.workflow.conformance.datastructure.interf;

/**
 * This data structure is used to execute algorithms independently of the AST representation of
 * BPMNs <a href="https://jgrapht.org/guide/UserOverview#graph-accessors">...</a>
 */
public interface WfGraph {

  void addNode(WfNode node);

  void addEdge(WfNode form, WfNode to);
}

package de.monticore.workflow.conformance.utils;

public class GraphError {
  public static String NODE_EDGE_NOT_PRESENT =
      "Unable to add edge (%s,%s), the node %s is not present in the graph";
  public static String NODE_DUPLICATION =
      "Unable to add node %s, a node with the same  name already exist in the graph";
}

package de.monticore.workflow.conformance.datastructure.interf;

public interface WfBuilder<Node> {

  /****
   * Transform a task to a Workflow node.
   * @param label the label of the task.
   */
  Node mkNamedTask(String label);

  /****
   * Transform an event to a Workflow node.
   * @param label the label of the event.
   */
  Node mkNamedEvent(String label);

  /****
   * Transform an gateway to a Workflow node.
   * @param label the label of the event.
   * @param type the type of the gateway.
   */
  Node mkNamedGateway(String label, NodeType type);

  /***
   * Transform the start event of the workflow  to a Workflow Node.
   * @param label the label of the start  event.
   */
  Node mkStartEvent(String label);

  /***
   * @return the start node of the Workflow.
   */
  Node build();

  //
  //  /*
  //  /***
  //   * Make a logical Xor scope from collection of alternatives.
  //   * @param label The label of the Xor gatter.
  //   * @param nodes the different alternatives as BPMNNode.
  //   */
  //  default Node mkXor(String label, List<Node> nodes) {
  //    return mkXor(Optional.of(label), nodes);
  //  }
  //
  //  /***
  //   * Make a logical Xor scope from collection of alternatives.
  //   * @param label The label of the Xor gatter.
  //   * @param nodes the different alternatives as BPMNNode.
  //   */
  //  Node mkXor(Optional<String> label, List<Node> nodes);
  //
  //  /***
  //   * Make a logical Xor scope from collection of alternatives.
  //   * @param nodes the different alternatives as BPMNNode.
  //   */
  //
  //  default Node mkXor(List<Node> nodes) {
  //    return mkXor(Optional.empty(), nodes);
  //  }
  //
  //  /***
  //   * transform a list of BPMNNode to build a sequence.
  //   * @param nodes the collection of BPMNNodes.
  //   */
  //  Node mkSequence(List<Node> nodes);
  //
  //  /***
  //   * Make a logical And-gatter from a collection of alternatives.
  //   * @param label the label of the And-gatter.
  //   * @param nodes the different alternatives as BPMNNodes.
  //   */
  //  Node mkAnd(Optional<String> label, List<Node> nodes);
  //
  //  /***
  //   * Make a logical And-gatter from a collection of alternatives.
  //   * @param label the label of the And-gatter.
  //   * @param nodes the different alternatives as BPMNNodes.
  //   */
  //  default Node mkAnd(String label, List<Node> nodes) {
  //    return mkAnd(Optional.of(label), nodes);
  //  }
  //
  //  /***
  //   * Make a logical And-gatter from a collection of alternatives.
  //   * @param nodes the different alternatives as BPMNNodes.
  //   */
  //  default Node mkAnd(List<Node> nodes) {
  //    return mkAnd(Optional.empty(), nodes);
  //  }
  //
  //  /***
  //   * Make an Or-gatter from a collection of alternatives.
  //   * @param label The label of the Or-gatter.
  //   * @param nodes the collection of alternatives.
  //   */
  //  Node mkOr(Optional<String> label, List<Node> nodes);
  //
  //  /***
  //   * Make an Or-gatter from a collection of alternatives.
  //   * @param label The label of the Or-gatter.
  //   * @param nodes the collection of alternatives.
  //   */
  //  default Node mkOr(String label, List<Node> nodes) {
  //    return mkOr(Optional.of(label), nodes);
  //  }
  //  /***
  //   * Make an Or-gatter from a collection of alternatives.
  //   * @param nodes the collection of alternatives.
  //   */
  //  default Node mkOr(List<Node> nodes) {
  //    return mkOr(Optional.empty(), nodes);
  //  }
  //
  //  // label optional
  //  Node mkLoop(String label, Node forward, Node backward);
  //
  //  default Node mkLoop(Node forward) {
  //    return mkLoop("", forward, mkSequence(new ArrayList<>()));
  //  }

}

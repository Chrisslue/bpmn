package de.monticore.workflow.conformance.datastructure.interf;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface WfBuilder<Node> {

  /****
   * transform a task to a BMPNNode.
   * @param name the name of the task.
   */
  NodeBuilder<Node> mkNamedTask(String name);

  /****
   * transform an event to a BMPNNode.
   * @param name the name of the event.
   */
  NodeBuilder<Node> mkNamedEvent(String name);

   //todo add  javadoc
  NodeBuilder<Node> mkNamedGateway(String name,NodeType type);

  /***
   * Make a logical Xor scope from collection of alternatives.
   * @param name The name of the Xor gatter.
   * @param nodes the different alternatives as BPMNNode.
   */
  default  NodeBuilder<Node> mkXor(String name, List<NodeBuilder<Node>> nodes) {
    return mkXor(Optional.of(name), nodes);
  }

  /***
   * Make a logical Xor scope from collection of alternatives.
   * @param name The name of the Xor gatter.
   * @param nodes the different alternatives as BPMNNode.
   */
  NodeBuilder<Node> mkXor(Optional<String> name, List<NodeBuilder<Node>> nodes);

  /***
   * Make a logical Xor scope from collection of alternatives.
   * @param nodes the different alternatives as BPMNNode.
   */

  default  NodeBuilder<Node> mkXor(List< NodeBuilder<Node>> nodes) {
    return mkXor(Optional.empty(), nodes);
  }

  /***
   * transform a list of BPMNNode to build a sequence.
   * @param nodes the collection of BPMNNodes.
   */
  NodeBuilder<Node> mkSequence(List< NodeBuilder<Node>> nodes);

  /***
   * Make a logical And-gatter from a collection of alternatives.
   * @param name the name of the And-gatter.
   * @param nodes the different alternatives as BPMNNodes.
   */
  NodeBuilder<Node> mkAnd(Optional<String> name, List< NodeBuilder<Node>> nodes);

  /***
   * Make a logical And-gatter from a collection of alternatives.
   * @param name the name of the And-gatter.
   * @param nodes the different alternatives as BPMNNodes.
   */
  default  NodeBuilder<Node> mkAnd(String name, List< NodeBuilder<Node>> nodes) {
    return mkAnd(Optional.of(name), nodes);
  }

  /***
   * Make a logical And-gatter from a collection of alternatives.
   * @param nodes the different alternatives as BPMNNodes.
   */
  default  NodeBuilder<Node> mkAnd(List< NodeBuilder<Node>> nodes) {
    return mkAnd(Optional.empty(), nodes);
  }

  /***
   * Make an Or-gatter from a collection of alternatives.
   * @param name The name of the Or-gatter.
   * @param nodes the collection of alternatives.
   */
  NodeBuilder<Node> mkOr(Optional<String> name, List< NodeBuilder<Node>> nodes);

  /***
   * Make an Or-gatter from a collection of alternatives.
   * @param name The name of the Or-gatter.
   * @param nodes the collection of alternatives.
   */
  default  NodeBuilder<Node> mkOr(String name, List< NodeBuilder<Node>> nodes) {
    return mkOr(Optional.of(name), nodes);
  }
  /***
   * Make an Or-gatter from a collection of alternatives.
   * @param nodes the collection of alternatives.
   */
  default  NodeBuilder<Node> mkOr(List< NodeBuilder<Node>> nodes) {
    return mkOr(Optional.empty(), nodes);
  }

  // name optional
  NodeBuilder<Node> mkLoop(String name, NodeBuilder<Node> forward, NodeBuilder<Node> backward);

  default  NodeBuilder<Node> mkLoop(NodeBuilder<Node> forward) {
    return mkLoop("", forward, mkSequence(new ArrayList<>()));
  }
}

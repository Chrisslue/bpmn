package de.monticore.workflow.conformance.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface BPMNBuilder<Node> {

  /****
   * transform a task to a BMPNNode.
   * @param name the name of the task.
   */
  Node mkTask(String name);

  /****
   * transform an event to a BMPNNode.
   * @param name the name of the event.
   */
  Node mkEvent(String name);

  /***
   * Make a logical Xor scope from collection of alternatives.
   * @param name The name of the Xor gatter.
   * @param nodes the different alternatives as BPMNNode.
   */
  default Node mkXor(String name, List<Node> nodes) {
    return mkXor(Optional.of(name), nodes);
  }

  /***
   * Make a logical Xor scope from collection of alternatives.
   * @param name The name of the Xor gatter.
   * @param nodes the different alternatives as BPMNNode.
   */
  Node mkXor(Optional<String> name, List<Node> nodes);

  /***
   * Make a logical Xor scope from collection of alternatives.
   * @param nodes the different alternatives as BPMNNode.
   */

  default Node mkXor(List<Node> nodes) {
    return mkXor(Optional.empty(), nodes);
  }

  /***
   * transform a list of BPMNNode to build a sequence.
   * @param nodes the collection of BPMNNodes.
   */
  Node mkSequence(List<Node> nodes);

  /***
   * Make a logical And-gatter from a collection of alternatives.
   * @param name the name of the And-gatter.
   * @param nodes the different alternatives as BPMNNodes.
   */
  Node mkAnd(Optional<String> name, List<Node> nodes);

  /***
   * Make a logical And-gatter from a collection of alternatives.
   * @param name the name of the And-gatter.
   * @param nodes the different alternatives as BPMNNodes.
   */
  default Node mkAnd(String name, List<Node> nodes) {
    return mkAnd(Optional.of(name), nodes);
  }

  /***
   * Make a logical And-gatter from a collection of alternatives.
   * @param nodes the different alternatives as BPMNNodes.
   */
  default Node mkAnd(List<Node> nodes) {
    return mkAnd(Optional.empty(), nodes);
  }

  /***
   * Make an Or-gatter from a collection of alternatives.
   * @param name The name of the Or-gatter.
   * @param nodes the collection of alternatives.
   */
  Node mkOr(Optional<String> name, List<Node> nodes);

  /***
   * Make an Or-gatter from a collection of alternatives.
   * @param name The name of the Or-gatter.
   * @param nodes the collection of alternatives.
   */
  default Node mkOr(String name, List<Node> nodes) {
    return mkOr(Optional.of(name), nodes);
  }
  /***
   * Make an Or-gatter from a collection of alternatives.
   * @param nodes the collection of alternatives.
   */
  default Node mkOr(List<Node> nodes) {
    return mkOr(Optional.empty(), nodes);
  }

  // name optional
  Node mkLoop(String name, Node forward, Node backward);

  default Node mkLoop(Node forward) {
    return mkLoop("", forward, mkSequence(new ArrayList<>()));
  }
}

package de.monticore.wf2lts.transformer;

import de.monticore.wf2lts.datastructure.LTS;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultParallelInterleaving {

  private final LTS oldLTs;

  private final LTS lts;

  private final MetaState metaRoot;

  public DefaultParallelInterleaving(LTS oldLTS) {
    var initialParallelTransitions = oldLTS.getOutgoings(oldLTS.getStart());

    this.lts = new LTS();
    this.metaRoot =
        new MetaState(
            initialParallelTransitions,
            Collections.emptyList(),
            Collections.emptyMap(),
            lts.getStart());
    this.oldLTs = oldLTS;
  }

  public static LTS interleave(LTS lts) {
    return new DefaultParallelInterleaving(lts).interleave();
  }

  private LTS interleave() {
    metaRoot.recursiveExpand();
    return lts;
  }

  /**
   * This class encapsulates the logic of interleaving. Every meta state is tied to a new state in
   * the interleaved lts. It contains additional information of which transitions could be expanded
   * next. Note that initialParallelTransitions and ltsReferences are kept separately as every
   * outgoing of every non-start state is treated as exclusive choice but the outgoings transitions
   * of the start state (of the oldLTS) are parallel.
   */
  private class MetaState {

    private final List<LTS.Transition> initialParallelTransitions;

    private final List<LTS.State> ltsReferences;

    private final Map<String, LTS.State> previous;

    private final LTS.State ltsState;

    /**
     * @param initialParallelTransitions The outgoings of the start-state form the oldLts that were
     *     not yet visited.
     * @param ltsReferences The other places in the oldLts where a transition could be chosen next.
     * @param previous Previously seen transition-label, kept in order to detect back references.
     * @param ltsState The corresponding lts state in the newly build interleaved lts.
     */
    private MetaState(
        List<LTS.Transition> initialParallelTransitions,
        List<LTS.State> ltsReferences,
        Map<String, LTS.State> previous,
        LTS.State ltsState) {
      this.initialParallelTransitions = initialParallelTransitions;
      this.ltsReferences = ltsReferences;
      this.previous = previous;
      this.ltsState = ltsState;
    }

    /**
     * Build the interleaved lts by successively expanding outgoing-transitions. At every point
     * either a direct successor of a reference is expanded or one of the initial outgoing
     * transitions of the start-state are expanded. 1. Expand all successors of all references. 2.
     * Expand all parallel initial edges.
     */
    public void recursiveExpand() {

      ltsReferences.forEach(this::expandReference);
      initialParallelTransitions.forEach(this::expandInitialParallelTransition);
    }

    private void expandInitialParallelTransition(LTS.Transition transition) {
      if (handleIfExists(transition)) {
        return;
      }
      var nextInitial = new ArrayList<>(initialParallelTransitions);
      nextInitial.remove(transition);
      var nextReferences = new ArrayList<>(ltsReferences);
      nextReferences.add(transition.getTarget());
      var nextMeta =
          new MetaState(nextInitial, nextReferences, new HashMap<>(previous), new LTS.State());
      nextMeta.previous.put(transition.getLabel(), nextMeta.ltsState);
      lts.addTransition(transition.changedSource(this.ltsState).changedTarget(nextMeta.ltsState));
      nextMeta.recursiveExpand();
    }

    private void expandReference(LTS.State stateReference) {
      for (var transition : oldLTs.getOutgoings(stateReference)) {
        if (handleIfExists(transition)) {
          continue;
        }
        var nextMeta = this.advancedBy(stateReference, transition);
        lts.addTransition(transition.changedSource(this.ltsState).changedTarget(nextMeta.ltsState));
        // Mark the nextState as target of future back-links.
        nextMeta.previous.put(transition.getLabel(), nextMeta.ltsState);
        nextMeta.recursiveExpand();
      }
    }

    private MetaState advancedBy(LTS.State expandedReference, LTS.Transition transition) {
      List<LTS.State> nextReferences = new ArrayList<>(this.ltsReferences);
      nextReferences.remove(expandedReference);
      nextReferences.add(transition.getTarget());
      var nextPreviousSeen = new HashMap<>(previous);
      var nextState = new LTS.State();
      return new MetaState(initialParallelTransitions, nextReferences, nextPreviousSeen, nextState);
    }

    private boolean handleIfExists(LTS.Transition transition) {
      if (transition.getSource().equals(transition.getTarget())) {
        throw new UnsupportedOperationException("TODO handle self-loop"); // TODO
      } else if (previous.containsKey(transition.getLabel())) { // Create back-link
        throw new UnsupportedOperationException("Backlinks are not yet implemented");
      } else {
        return false;
      }
    }
  }
}

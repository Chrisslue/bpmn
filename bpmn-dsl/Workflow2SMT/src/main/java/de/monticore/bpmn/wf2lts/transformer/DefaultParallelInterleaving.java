package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultParallelInterleaving {

  protected final LTS oldLTs;

  protected final LTS lts;

  protected final MetaState metaRoot;

  protected List<DetectedCycle> detectedCycles;

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
    this.detectedCycles = new ArrayList<>();
  }

  public static LTS interleave(LTS lts) {
    return new DefaultParallelInterleaving(lts).interleave();
  }

  protected LTS interleave() {
    metaRoot.recursiveExpand();
    resolveCycle();
    return lts;
  }

  private void resolveCycle() {
    var traverser = new LTSTraverser(lts);
    for (DetectedCycle detectedCycle : detectedCycles) {
      var listOfLabel = detectedCycle.pathInNewWithoutCycle();
      var pathToTarget = traverser
          .pathOfLabel(listOfLabel)
          .orElseThrow(() -> new IllegalStateException("Could not find cycle target"));
      var target = pathToTarget.getLastState();
      lts.addTransition(
          detectedCycle.cycleClosingTransition
              .changedSource(detectedCycle.stateInNewLTs)
              .changedTarget(target)
      );
    }
  }

  /**
   * This class encapsulates the logic of interleaving. Every meta state is tied to a new state in the interleaved lts.
   * It contains additional information of which transitions could be expanded next. Note that
   * initialParallelTransitions and ltsReferences are kept separately as every outgoing of every non-start state is
   * treated as exclusive choice but the outgoings transitions of the start state (of the oldLTS) are parallel.
   */
  protected class MetaState {

    protected final List<LTS.Transition> initialParallelTransitions;

    protected final List<LTS.State> ltsReferences;

    protected final Map<String, LTS.State> previous;

    protected final LTS.State ltsState;

    /**
     * @param initialParallelTransitions The outgoings of the start-state form the oldLts that were not yet visited.
     * @param ltsReferences              The other places in the oldLts where a transition could be chosen next.
     * @param previous                   Previously seen transition-label, kept in order to detect back references.
     * @param ltsState                   The corresponding lts state in the newly build interleaved lts.
     */
    protected MetaState(
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
     * Build the interleaved lts by successively expanding outgoing-transitions. At every point either a direct
     * successor of a reference is expanded or one of the initial outgoing transitions of the start-state are expanded.
     * 1. Expand all successors of all references. 2. Expand all parallel initial edges.
     */
    public void recursiveExpand() {

      ltsReferences.forEach(this::expandReference);
      initialParallelTransitions.forEach(this::expandInitialParallelTransition);
    }

    protected void expandInitialParallelTransition(LTS.Transition transition) {
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

    protected void expandReference(LTS.State stateReference) {
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

    protected MetaState advancedBy(LTS.State expandedReference, LTS.Transition transition) {
      List<LTS.State> nextReferences = new ArrayList<>(this.ltsReferences);
      nextReferences.remove(expandedReference);
      nextReferences.add(transition.getTarget());
      var nextPreviousSeen = new HashMap<>(previous);
      var nextState = new LTS.State();
      return new MetaState(initialParallelTransitions, nextReferences, nextPreviousSeen, nextState);
    }

    protected boolean handleIfExists(LTS.Transition transition) {
      if (transition.getSource().equals(transition.getTarget())) {
        lts.addTransition(transition.changedSource(ltsState).changedTarget(ltsState));
        return true;
      } else if (previous.containsKey(transition.getLabel())) { // Mark transition as detected cycle
        handleCycle(transition);
        return true;
      } else {
        return false;
      }
    }

    protected void handleCycle(LTS.Transition cycleClosingTransitionOfOldLTS) {
      var cycleInOld = findCycleInOld(cycleClosingTransitionOfOldLTS);
      detectedCycles.add(new DetectedCycle(cycleInOld, this.ltsState, cycleClosingTransitionOfOldLTS));
    }

    protected Path findCycleInOld(LTS.Transition cycleClosingTransitionOfOldLTS) {
      var oldTraverser = new LTSTraverser(oldLTs);
      var cycleInOldLTS = oldTraverser.pathsBetween(cycleClosingTransitionOfOldLTS.getTarget(),
          cycleClosingTransitionOfOldLTS.getSource());
      if (cycleInOldLTS.size() != 1) {
        throw new IllegalStateException(
            "Detected cycle with " + cycleClosingTransitionOfOldLTS + " but could not recreate it in " + oldLTs
                + " found " + cycleInOldLTS.size() + " cycle.");
      }
      return cycleInOldLTS.get(0).advancedBy(cycleClosingTransitionOfOldLTS);
    }
  }

  protected class DetectedCycle {

    private final Path cycleInOld;
    private final LTS.State stateInNewLTs;

    private final LTS.Transition cycleClosingTransition;

    public DetectedCycle(Path cycleInOld, State stateInNewLTs, Transition cycleClosingTransition) {
      this.cycleInOld = cycleInOld;
      this.stateInNewLTs = stateInNewLTs;
      this.cycleClosingTransition = cycleClosingTransition;
    }

    private Path pathFromStartInNew() {
      return new LTSTraverser(lts).pathsBetween(lts.getStart(), stateInNewLTs)
          .stream()
          .min(Comparator.comparingInt(p -> p.getTransitions().size()))
          .orElseThrow(() -> new IllegalStateException("Could not find path from start to state: " + stateInNewLTs));
    }

    private List<String> pathInNewWithoutCycle() {
      var pathLabelInNew = pathFromStartInNew().asLabel();

      var cycleLabel = cycleInOld.asLabel();

      if (!cycleLabel.get(cycleLabel.size() - 1).equals(cycleClosingTransition.getLabel())) {
        throw new IllegalStateException(
            "Cycle in old lts does not end with cycleClosingTransition" + cycleLabel
                + " should end with " + cycleClosingTransition.getLabel());
      }

      List<String> pathInNewWithoutCycle = new ArrayList<>();
      int cycleIndex = 0;
      for (String label : pathLabelInNew) {
        if (cycleLabel.get(cycleIndex).equals(label)) {
          cycleIndex++;
        } else if (cycleIndex != 0 && cycleIndex != cycleLabel.size() - 1) {
          throw new IllegalStateException("Cycle in old lts not contained in path of new lts."
              + "Cycle in old was: " + cycleLabel + " path in new was: " + pathLabelInNew);
        } else {
          pathInNewWithoutCycle.add(label);
        }
      }
      return pathInNewWithoutCycle;
    }
  }
}

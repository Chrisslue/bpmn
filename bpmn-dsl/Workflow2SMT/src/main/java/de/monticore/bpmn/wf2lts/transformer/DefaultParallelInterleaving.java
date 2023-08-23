package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser.Path;
import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Stream;

public class DefaultParallelInterleaving {

  protected final LTSWithFinalStates oldLTS;


  protected final LTSWithFinalStates lts;


  protected final MetaState metaRoot;

  protected List<DetectedCycle> detectedCycles;

  public DefaultParallelInterleaving(LTSWithFinalStates oldLTSWithFinalStates) {
    this.oldLTS = oldLTSWithFinalStates;
    var initialParallelTransitions = oldLTS.getOutgoings(oldLTS.getStart());

    this.lts = new LTSWithFinalStates();
    this.metaRoot =
        new MetaState(
            initialParallelTransitions,
            Collections.emptyList(),
            Collections.emptySet(), Collections.emptySet(), lts.getStart(),
            oldLTS.isFinalState(oldLTS.getStart())
        );
    this.detectedCycles = new ArrayList<>();
  }

  public static LTSWithFinalStates interleave(LTSWithFinalStates lts) {
    return new DefaultParallelInterleaving(lts).interleave();
  }

  protected LTSWithFinalStates interleave() {
    if (!oldLTS.getIncoming(oldLTS.getStart()).isEmpty()) {
      throw new IllegalArgumentException(
          "Start state " + oldLTS.getStart() + " of lts " + oldLTS + "had incoming transitions."
              + " see the Readme section about assumptions over interleaving strategies."
      );
    }
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

    protected final Set<LTS.State> visitedStates;

    protected final Set<Pair<LTS.State,LTS.State>> visitedTransition;

    protected final LTS.State ltsState;

    protected final boolean potentialFinalState;

    /**
     * @param initialParallelTransitions The outgoings of the start-state form the oldLts that were
     *                                   not yet visited.
     * @param ltsReferences              The other places in the oldLts where a transition could be
     *                                   chosen next.
     * @param visitedStates              Previously seen transition-label, kept in order to detect
     *                                   back references.
     * @param visitedTransition
     * @param ltsState                   The corresponding lts state in the newly build interleaved
     *                                   lts.
     * @param potentialFinalState        If the transition expanded led to a final state ind oldLTS
     *                                   mark this as potential final state.
     */
    protected MetaState(
        List<Transition> initialParallelTransitions,
        List<State> ltsReferences,
        Set<State> visitedStates, Set<Pair<State, State>> visitedTransition, State ltsState,
        boolean potentialFinalState
    ) {
      this.initialParallelTransitions = initialParallelTransitions;
      this.ltsReferences = ltsReferences;
      this.visitedStates = visitedStates;
      this.visitedTransition = visitedTransition;
      this.ltsState = ltsState;
      this.potentialFinalState = potentialFinalState;
    }

    /**
     * Build the interleaved lts by successively expanding outgoing-transitions. At every point either a direct
     * successor of a reference is expanded or one of the initial outgoing transitions of the start-state are expanded.
     * 1. Expand all successors of all references. 2. Expand all parallel initial edges.
     */
    public void recursiveExpand() {

      // When the corresponding state in the old lts was marked final:
      if (this.potentialFinalState) {
        // If we have no more outgoing transitions, that we have to visit, mark it as a final state.
        boolean isFinalState =
            Stream.concat(
                ltsReferences.stream().flatMap(state -> oldLTS.getOutgoings(state).stream()),
                initialParallelTransitions.stream()
            ).allMatch(this::isCycle);
        if (isFinalState) {
          lts.addAsFinalState(this.ltsState);
        }
      }

      ltsReferences.forEach(this::expandReference);
      initialParallelTransitions.forEach(this::expandInitialParallelTransition);
    }

    protected void expandInitialParallelTransition(LTS.Transition transition) {
      if (isCycle(transition)) {
        handleCyclicTransition(transition);
        return;
      }
      var nextInitial = new ArrayList<>(initialParallelTransitions);
      nextInitial.remove(transition);
      var nextReferences = new ArrayList<>(ltsReferences);
      nextReferences.add(transition.getTarget());
      var nextMeta =
          new MetaState(
              nextInitial,
              nextReferences,
              new HashSet<>(visitedStates), new HashSet<>(visitedTransition), new LTS.State(),
              oldLTS.isFinalState(transition.getTarget())
          );
      nextMeta.visitedStates.add(transition.getTarget());
      nextMeta.visitedTransition.add(Pair.of(transition.getSource(),transition.getTarget()));
      lts.addTransition(transition.changedSource(this.ltsState).changedTarget(nextMeta.ltsState));
      nextMeta.recursiveExpand();
    }

    protected void expandReference(LTS.State stateReference) {
      for (var transition : oldLTS.getOutgoings(stateReference)) {
        if (isCycle(transition)) {
          handleCyclicTransition(transition);
          continue;
        }
        var nextMeta = this.advancedBy(stateReference, transition);
        lts.addTransition(transition.changedSource(this.ltsState).changedTarget(nextMeta.ltsState));
        // Mark the nextState as target of future back-links.
        nextMeta.visitedStates.add(transition.getTarget());
        nextMeta.visitedTransition.add(Pair.of(transition.getSource(),transition.getTarget()));
        nextMeta.recursiveExpand();
      }
    }

    protected MetaState advancedBy(LTS.State expandedReference, LTS.Transition transition) {
      List<LTS.State> nextReferences = new ArrayList<>(this.ltsReferences);
      nextReferences.remove(expandedReference);
      nextReferences.add(transition.getTarget());
      var nextVisitedStates = new HashSet<>(visitedStates);
      var nextState = new LTS.State();
      return new MetaState(
          initialParallelTransitions,
          nextReferences,
          nextVisitedStates, visitedTransition, nextState,
          oldLTS.isFinalState(transition.getTarget())
      );
    }

    protected boolean isCycle(LTS.Transition transition) {
      return transition.getSource().equals(transition.getTarget())
          || (visitedStates.contains(transition.getTarget())
          && visitedTransition.stream().anyMatch(p -> p.getLeft().equals(transition.getSource())
          && p.getRight().equals(transition.getTarget())));
    }

    protected void handleCyclicTransition(LTS.Transition transition) {
      if (transition.getSource().equals(transition.getTarget())) {
        lts.addTransition(transition.changedSource(ltsState).changedTarget(ltsState));
      } else if (visitedStates.contains(transition.getTarget())) { // Mark transition as detected cycle
        var cycleInOld = findCycleInOld(transition);
        detectedCycles.add(new DetectedCycle(cycleInOld, this.ltsState, transition));
      }
    }


    protected Path findCycleInOld(LTS.Transition cycleClosingTransitionOfOldLTS) {
      var oldTraverser = new LTSTraverser(oldLTS);
      var cycleInOldLTS = oldTraverser.pathsBetween(cycleClosingTransitionOfOldLTS.getTarget(),
          cycleClosingTransitionOfOldLTS.getSource());
      if (cycleInOldLTS.size() != 1) {
        throw new IllegalStateException(
            "Detected cycle with " + cycleClosingTransitionOfOldLTS + " but could not recreate it in " + oldLTS
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
        if (cycleIndex < cycleLabel.size() && cycleLabel.get(cycleIndex).equals(label)) {
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

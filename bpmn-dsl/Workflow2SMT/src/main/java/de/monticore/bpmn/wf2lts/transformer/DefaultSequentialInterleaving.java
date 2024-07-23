package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Interleaving strategy designed for inclusive gateways. At an inclusive split-gateway in BPMN
 * every number of outgoing transitions can be taken. This strategy implements this in a sequential
 * way, such that one path has to be finished before the next begins. For example: s0 --> s1: A s0
 * --> s2: B s1 --> s3: C s2 --> s4: D Will become: s0 --> s1: A s1 --> s3: C s3 --> s5: B s5 -->
 * s6: D s0 --> s2: B s2 --> s4: D s3 --> s7: A s7 --> s8: B Even tho the strategy is especially
 * suited to be used for inclusive-gateways it could be also used for parallel gateways if desired.
 */
public class DefaultSequentialInterleaving {

  private final LTSWithFinalStates oldLTS;

  private final LTSWithFinalStates lts;

  public DefaultSequentialInterleaving(LTSWithFinalStates oldLTS) {
    this.oldLTS = oldLTS;
    this.lts = new LTSWithFinalStates(oldLTS.getStart());
    this.lts.addLTS(oldLTS);
  }

  public static LTSWithFinalStates interleave(LTSWithFinalStates lts) {
    return new DefaultSequentialInterleaving(lts).interleave();
  }

  private LTSWithFinalStates interleave() {
    if (!oldLTS.getIncoming(oldLTS.getStart()).isEmpty()) {
      throw new IllegalArgumentException(
          "Start state "
              + oldLTS.getStart()
              + " of lts "
              + oldLTS
              + "had incoming transitions."
              + " see the Readme section about assumptions over interleaving strategies.");
    }
    var startTransitions = oldLTS.getOutgoings(oldLTS.getStart());

    // Get the part of the old lts induced by the initial transition from start.
    List<LTSWithFinalStates> subLTSCopies =
        startTransitions.stream()
            .map(transition -> subLTS(oldLTS, transition))
            .collect(Collectors.toList());

    for (int i = 0; i < startTransitions.size(); i++) {
      var otherSubLTS = new ArrayList<>(subLTSCopies);
      otherSubLTS.remove(i);
      // Add the other sub-lts to each final state of the current sub-lts
      for (var finalState : subLTSCopies.get(i).getFinalStates()) {
        recursiveAppend(finalState, otherSubLTS);
      }
    }
    return lts;
  }

  /*
   * Append every lts as possible continuation to the final state.
   * If more than one element is in the list recursively add the remaining options to the final states of the added lts.
   */
  private void recursiveAppend(State finalState, List<LTSWithFinalStates> remainingLTS) {
    for (LTSWithFinalStates subLTS : remainingLTS) {
      var deepCopy = new LTSWithFinalStates(subLTS);
      appendLTSToFinalState(finalState, deepCopy);
      for (var subLTSFinalState : deepCopy.getFinalStates()) {
        var nextRemaining = new ArrayList<>(remainingLTS);
        nextRemaining.remove(subLTS);
        recursiveAppend(subLTSFinalState, nextRemaining);
      }
    }
  }

  /**
   * Include the subLTS into lts but with finalState as start-state.
   *
   * @param finalState the new start-state of subLTS.
   * @param subLTS the lts that should be appended as outgoing of finalState in lts.
   */
  private void appendLTSToFinalState(State finalState, LTSWithFinalStates subLTS) {
    for (var startTransition : subLTS.getOutgoings(subLTS.getStart())) {
      lts.addTransition(startTransition.changedSource(finalState));
    }
    lts.addLTS(subLTS);
    // Remove start state and its outgoings.
    lts.removeState(subLTS.getStart());
  }

  /**
   * Create the sub-lts induced by the startTransition. We want startTransition to be in the sub-lts
   * but no other transition outgoing from startTransition.getSource().
   */
  public static LTSWithFinalStates subLTS(LTSWithFinalStates lts, Transition startTransition) {
    var subLTSFromTarget = new LTSTraverser(lts).subLTS(startTransition.getTarget());
    // Create a copy of the subLTS-part.
    var subLTS = new LTSWithFinalStates(startTransition.getSource());

    subLTS.addTransition(startTransition);
    subLTS.addTransitionsOf(subLTSFromTarget);
    // Add all final states of lts that are present in subLTSFromTarget to subLTS.
    subLTSFromTarget.getStates().stream()
        .filter(state -> lts.getFinalStates().contains(state))
        .forEach(subLTS::addAsFinalState);
    return subLTS;
  }
}

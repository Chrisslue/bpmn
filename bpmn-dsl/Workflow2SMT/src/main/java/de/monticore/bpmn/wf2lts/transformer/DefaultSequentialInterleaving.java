package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.datastructure.LTS.State;
import de.monticore.bpmn.wf2lts.datastructure.LTS.Transition;
import de.monticore.bpmn.wf2lts.datastructure.LTSTraverser;
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

  private final LTS oldLTS;

  private final LTS lts;

  public DefaultSequentialInterleaving(LTS oldLTS) {
    this.oldLTS = oldLTS;
    this.lts = new LTS(oldLTS.getStart());
    this.lts.addLTS(oldLTS);
  }

  public static LTS interleave(LTS lts) {
    return new DefaultSequentialInterleaving(lts).interleave();
  }

  private LTS interleave() {
    // TODO What about transitions back to oldLTS.getStart()?
    // TODO States that only have back-links should be considered terminal too?
    var startTransitions = oldLTS.getOutgoings(oldLTS.getStart());

    List<LTS> subLTSCopies =
        startTransitions.stream()
            .map(transition -> subLTS(oldLTS, transition))
            .collect(Collectors.toList());

    for (int i = 0; i < startTransitions.size(); i++) {
      var transition = startTransitions.get(i);
      var otherSubLTS = new ArrayList<>(subLTSCopies);
      otherSubLTS.remove(i);
      for (var terminal : reachableTerminalStates(oldLTS, transition.getTarget())) {
        recursiveAppend(terminal, otherSubLTS);
      }
    }
    return lts;
  }

  /*
   * Append every lts as possible continuation to the terminal state.
   * If more than one element is in the list recursively add the remaining options to the terminal states of the added lts.
   */
  private void recursiveAppend(State terminal, List<LTS> remainingLTS) {
    for (LTS subLTS : remainingLTS) {
      var copy = new LTS(subLTS);
      addLTSToTerminal(terminal, copy);
      for (var subLTSTerminal : copy.getTerminalStates()) {
        var nextRemaining = new ArrayList<>(remainingLTS);
        nextRemaining.remove(subLTS);
        recursiveAppend(subLTSTerminal, nextRemaining);
      }
    }
  }

  private void addLTSToTerminal(State terminal, LTS subLTS) {
    for (var startTransition : subLTS.getOutgoings(subLTS.getStart())) {
      lts.addTransition(startTransition.changedSource(terminal));
    }
    lts.addLTS(subLTS);
    // Remove start state and its outgoings.
    lts.removeState(subLTS.getStart());
  }

  public static LTS subLTS(LTS lts, Transition startTransition) {
    var subLTSFromTarget = new LTSTraverser(lts).subLTS(startTransition.getTarget());
    var subLTS = new LTS(startTransition.getSource());
    subLTS.addTransition(startTransition);
    subLTS.addTransitionsOf(subLTSFromTarget);
    return subLTS;
  }

  /**
   * Use depth-first-search to find all terminal states in oldLTS that are reachable from state.
   *
   * @return A list of all found terminal states.
   */
  public static List<State> reachableTerminalStates(LTS oldLTS, State state) {

    List<State> foundTerminalStates = new ArrayList<>();
    new LTSTraverser(oldLTS).depthFirstSearchLTS(
        state,
        (s) -> {
          if (oldLTS.getOutgoings(s).isEmpty()) {
            foundTerminalStates.add(s);
          }
        });
    return foundTerminalStates;
  }

}

package de.monticore.wf2lts.datastructure;

import de.monticore.wf2lts.datastructure.LTS.State;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LTSTraverser {

  public LTSTraverser(LTS lts) {
    this.lts = lts;
  }

  private final LTS lts;

  /**
   * Traverse a lst given a list of transition label. This works for deterministic as well as non-determinist lts
   * (w.r.t. the outgoing transition-label).
   *
   * @param lts    The lts to be traversed.
   * @param labels List of transition label.
   * @return The path of visited transitions if a path could be found else empty.
   */
  public static Optional<Path> pathOfLabel(LTS lts, List<String> labels) {
    var traverser = new LTSTraverser(lts);
    return traverser.pathOfLabel(labels);
  }

  /**
   * Use depth-first-search to compute the reachable part of the lts. The returned lts will have the exact same states
   * and transitions (no copies).
   *
   * @param fromState The start point from which the search is started.
   * @return A view of the reachable part of the lts starting at fromState.
   */
  public LTS subLTS(State fromState) {

    ArrayList<State> visitedStates = new ArrayList<>();
    depthFirstSearchLTS(fromState, visitedStates::add);

    LTS subLTS = new LTS(fromState);
    visitedStates.forEach(state -> lts.getOutgoings(state).forEach(subLTS::addTransition));
    return subLTS;
  }

  public void depthFirstSearchLTS(State state, Consumer<State> visitingConsumer) {
    Deque<State> queue = new ArrayDeque<>();
    Set<State> visited = new HashSet<>();
    queue.add(state);

    while (!queue.isEmpty()) {
      var currentState = queue.removeFirst();
      if (visited.contains(currentState)) {
        continue;
      }
      visited.add(currentState);

      visitingConsumer.accept(currentState);

      var outgoings = lts.getOutgoings(currentState);
      outgoings.forEach(transition -> queue.addFirst(transition.getTarget()));
    }
  }

  public List<Transition> outgoingsWith(State state, String label) {
    return streamOutgoingsWith(state, label)
        .collect(Collectors.toList());
  }

  protected Stream<Transition> streamOutgoingsWith(State state, String label) {
    return lts.getOutgoings(state)
        .stream()
        .filter(transition -> transition.getLabel().equals(label));
  }

  public Optional<Path> pathOfLabel(List<String> labels) {
    var path = new Path();

    String[] reversed = new String[labels.size()];
    for (int i = 1; i <= labels.size(); i++) {
      reversed[labels.size() - i] = labels.get(i - 1);
    }
    return path.findPathRecursive(reversed, reversed.length);
  }

  public class Path {

    private final List<Transition> transitions;

    public Path() {
      this(new ArrayList<>());
    }

    public List<Transition> getTransitions() {
      return transitions;
    }

    public Path(List<Transition> transitions) {
      this.transitions = transitions;
    }

    public Path(Path old, Transition next) {
      this.transitions = new ArrayList<>(old.transitions);
      if (!transitions.isEmpty() && transitions.get(transitions.size() - 1).getTarget() != next.getSource()) {
        throw new IllegalArgumentException("Transition is not a continuation of path");
      }
      transitions.add(next);
    }

    public Optional<State> getCurrentState() {
      return transitions.isEmpty() ? Optional.empty()
          : Optional.of(transitions.get(transitions.size() - 1).getTarget());
    }

    public Optional<Path> takeFirst(String label) {
      return getCurrentState()
          .flatMap(s -> outgoingsWith(s, label).stream().findFirst())
          .map(nextState -> new Path(this, nextState));
    }

    public boolean endsInTerminal() {
      return getCurrentState().map(s -> lts.getTerminalStates().contains(s)).orElse(false);
    }

    private Optional<Path> findPathRecursive(String[] remainingLabel, int size) {
      if (size == 0) {
        return Optional.of(this);
      }
      var nextLabel = remainingLabel[size - 1];

      var state = getCurrentState().isEmpty() ? lts.getStart() : getCurrentState().get();

      List<Transition> continuations = outgoingsWith(state, nextLabel);

      for (var nextTransition : continuations) {
        var optResult = new Path(this, nextTransition).findPathRecursive(remainingLabel, size - 1);
        if (optResult.isPresent()) {
          return optResult;
        }
      }
      return Optional.empty();

      /* Written with streams:
       * return continuations
       *           .stream()
       *           .map(transition ->new Path(this, transition).findPathRecursive(remainingLabel, size - 1))
       *           .filter(Optional::isPresent)
       *           .findAny()
       *           .flatMap(Function.identity());
       *
       */

    }

  }

}

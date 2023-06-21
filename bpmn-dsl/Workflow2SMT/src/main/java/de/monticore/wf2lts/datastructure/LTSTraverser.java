package de.monticore.wf2lts.datastructure;

import de.monticore.wf2lts.datastructure.LTS.State;
import de.monticore.wf2lts.datastructure.LTS.Transition;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
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

  /**
   * Generate all paths starting from source and ending in target. This method is not optimized and can result in
   * OutOfMemory exceptions for huge lts.
   */
  public List<Path> pathsBetween(State source, State target) {
    var foundPaths = new ArrayList<Path>();
    new Path(source).pathsBetweenRecursive(target, Collections.emptyList(), foundPaths);
    return foundPaths;
  }

  public Optional<Path> pathOfLabel(State from, List<String> labels) {
    var path = new Path(from);

    String[] reversed = new String[labels.size()];
    for (int i = 1; i <= labels.size(); i++) {
      reversed[labels.size() - i] = labels.get(i - 1);
    }

    return path.findPathOfLabelRecursive(reversed, reversed.length);
  }

  public Optional<Path> pathOfLabel(List<String> labels) {
    return pathOfLabel(lts.getStart(), labels);
  }

  public class Path {

    private final List<Transition> transitions;

    private final State lastState;

    public Path() {
      this(new ArrayList<>(), lts.getStart());
    }

    public Path(State lastState) {
      this(new ArrayList<>(), lastState);
    }

    public Path(List<Transition> transitions) {
      this(transitions, transitions.get(transitions.size() - 1).getTarget());
    }

    protected Path(List<Transition> transitions, State lastState) {
      this.transitions = transitions;
      this.lastState = lastState;
    }

    public List<Transition> getTransitions() {
      return transitions;
    }

    public State getLastState() {
      return lastState;
    }

    public Stream<Transition> stream() {
      return getTransitions().stream();
    }

    public List<String> asLabel() {
      return stream()
          .map(Transition::getLabel)
          .collect(Collectors.toList());
    }

    public Path advancedBy(Transition transition) {
      if (!transitions.isEmpty() && getLastState() != transition.getSource()) {
        throw new IllegalArgumentException("Transition is not a continuation of path");
      }
      var nextTransitions = new ArrayList<>(this.transitions);

      nextTransitions.add(transition);
      return new Path(nextTransitions);
    }

    public List<Transition> outgoingsWith(String label) {
      return outgoingsWithStream(label)
          .collect(Collectors.toList());
    }

    public Stream<Transition> outgoingsWithStream(String label) {
      return outgoings()
          .stream()
          .filter(transition -> transition.getLabel().equals(label));
    }

    public List<Transition> outgoings() {
      return lts.getOutgoings(getLastState());
    }

    public boolean endsInTerminal() {
      return lts.getTerminalStates().contains(getLastState());
    }
    //

    private Optional<Path> findPathOfLabelRecursive(String[] remainingLabel, int size) {
      if (size == 0) {
        return Optional.of(this);
      }
      var nextLabel = remainingLabel[size - 1];

      return this.outgoingsWithStream(nextLabel)
          .map(nextTransition -> this.advancedBy(nextTransition).findPathOfLabelRecursive(remainingLabel, size - 1))
          .filter(Optional::isPresent)
          .map(Optional::get)
          .findAny();
    }

    private void pathsBetweenRecursive(
        State target,
        List<State> visited,
        List<Path> foundPaths) {

      for (var transition : this.outgoings()) {
        if (transition.getTarget().equals(target)) {
          foundPaths.add(this.advancedBy(transition));
          return;
        }
        if (visited.contains(transition.getTarget())) {
          continue;
        }
        var nextVisited = new ArrayList<>(visited);
        nextVisited.add(transition.getTarget());
        this.advancedBy(transition)
            .pathsBetweenRecursive(target, nextVisited, foundPaths);
      }
    }

    public boolean labelOccurred(String label) {
      return this.transitions.stream().anyMatch(transition -> transition.getLabel().equals(label));
    }

  }
}

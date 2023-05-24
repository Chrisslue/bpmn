package de.monticore.bpmn.analysis.lola;

import com.google.common.base.Joiner;
import de.monticore.bpmn.analysis.petrinet.WorkflowNet;
import java.util.stream.Collectors;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

/** LoLA CTL* formulae for Workflow-net properties. */
public class LoLaFormulae {

  // LoLa may not terminate for liveness queries on petri nets containing cycles (depending on state
  // exploration order)
  // SEEMS TO BE BEST, combined with timeout to bail out if stuck
  public static String completion(final WorkflowNet wfNet) { // check liveness of final marking
    return "AG EF "
        + "("
        + wfNet.getSource().getName()
        + " <= 0"
        + " AND "
        + Joiner.on(" AND ")
            .join(
                wfNet.getPlacesNotSourceOrSink().stream()
                    .map(ASTPlace::getName)
                    .map(name -> name + " <= 0")
                    .collect(Collectors.toList()))
        + " AND "
        + wfNet.getSink().getName()
        + " > 0" // proper completion is than guaranteed by safeness check (option to complete +
                 // safeness => proper completion)
        + ")";
  }

  // Does not work in case of a live lock (i. e. some part of the net is still "alive" but no more
  // global progress is possible), e. g. infinite loop
  public static String completionReachability(final WorkflowNet wfNet) {
    return "EF (DEADLOCK AND NOT ("
        + wfNet.getSource().getName()
        + " <= 0"
        + " AND "
        + Joiner.on(" AND ")
            .join(
                wfNet.getPlacesNotSourceOrSink().stream()
                    .map(ASTPlace::getName)
                    .map(name -> name + " <= 0")
                    .collect(Collectors.toList()))
        + " AND "
        + wfNet.getSink().getName()
        + " > 0" // proper completion is than guaranteed by safeness check (option to complete +
                 // safeness => proper completion)
        + "))";
  }

  public static String unsafe(final ASTPlace place) {
    return "EF " + place.getName() + " > 1";
  }

  public static String safe(final ASTPlace place) {
    return "AG " + place.getName() + " <= 1";
  }

  public static String dead(
      final ASTTransition
          transition) { // instead of checking liveness directly we check if t is not enabled in the
                        // start marking
    return "AG NOT FIREABLE(" + transition.getName() + ")";
  }

  public static String live(
      final ASTTransition
          transition) { // instead of checking liveness directly we check if t is not enabled in the
                        // start marking
    return "EF FIREABLE(" + transition.getName() + ")";
  }
}

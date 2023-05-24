package de.monticore.bpmn.analysis.petrinet;

import static com.google.common.base.Preconditions.checkNotNull;

import de.monticore.prettyprint.IndentPrinter;
import java.util.Map;
import java.util.stream.Collectors;
import petrinet._ast.ASTEdge;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

/**
 * Prints a Petri net into the APT format.
 *
 * @see de.monticore.bpmn.analysis.apt.AptChecker
 */
public class PetriNetAptPrinter {

  public String print(final ASTPetrinet net) {
    return print(net, null);
  }

  public String print(final ASTPetrinet net, final Map<ASTPlace, Long> marking) {
    checkNotNull(net);

    final IndentPrinter out = new IndentPrinter();

    out.clearBuffer();
    out.setIndentLength(4);

    out.print(".name ");
    out.println("\"" + net.getName() + "\"");
    out.println(".type LPN");
    out.println();

    // PRINT PACES
    out.println(".places");
    net.streamPlaces().map(ASTPlace::getName).forEach(out::println);
    out.println();

    // PRINT TRANSITIONS
    out.println(".transitions");
    net.streamTransitions().map(ASTTransition::getName).forEach(out::println);
    out.println();

    // PRINT ARCS
    out.println(".flows");
    net.getTransitionList()
        .forEach(
            transition -> {
              out.print(transition.getName());
              out.print(": ");

              out.print("{");
              out.print(
                  transition
                      .streamFromEdges()
                      .map(ASTEdge::getPlace)
                      .collect(Collectors.joining(", ")));
              out.print("}");

              out.print(" -> ");

              out.print("{");
              out.print(
                  transition
                      .streamToEdges()
                      .map(ASTEdge::getPlace)
                      .collect(Collectors.joining(", ")));
              out.println("}");
            });
    out.println();

    // PRINT MARKING

    if (marking != null && !marking.isEmpty()) {
      out.print(".initial_marking ");
      final String markings =
          marking.entrySet().stream()
              .map(e -> e.getValue() + "*" + e.getKey().getName())
              .collect(Collectors.joining(", "));
      out.print("{");
      out.print(markings);
      out.println("}");
    }

    return out.getContent();
  }
}

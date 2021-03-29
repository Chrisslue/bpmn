package de.monticore.bpmn.analysis.petrinet;

import de.monticore.prettyprint.IndentPrinter;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;

import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Prints a Petri net into the LoLA format.
 *
 * @see de.monticore.bpmn.analysis.lola.LoLaChecker
 */
public class PetriNetLoLaPrinter {

    public String print(final ASTPetrinet net) {
        return print(net, null);
    }

    public String print(final ASTPetrinet net, final Map<ASTPlace, Long> marking) {
        checkNotNull(net);

        final IndentPrinter out = new IndentPrinter();

        out.clearBuffer();
        out.setIndentLength(4);

        // PRINT PLACES
        out.println("PLACE");
        out.indent();

        final String places = net.streamPlaces().map(ASTPlace::getName)
                .collect(Collectors.joining(", "));
        out.print(places);
        out.println(";");

        out.unindent();
        out.println();


        // PRINT MARKING
        out.println("MARKING");
        out.indent();
        if (marking != null && !marking.isEmpty()) {
            final String markings = marking.entrySet().stream().map(e -> e.getKey().getName() + ": " + e.getValue())
                    .collect(Collectors.joining(", "));
            out.print(markings);
        }
        out.println(";");
        out.unindent();
        out.println();


        // PRINT TRANSITIONS
        net.streamTransitions().forEach(t -> {
            out.print("TRANSITION ");
            out.println(t.getName());
            out.indent();

            out.print("CONSUME ");
            final String incomingArcs = t.streamFromEdges().map(e -> e.getPlace() + ": " + e.getCount().getValue())
                    .collect(Collectors.joining(", "));
            out.print(incomingArcs);
            out.println(";");

            out.print("PRODUCE ");
            final String outgoingArcs = t.streamToEdges().map(e -> e.getPlace() + ": " + e.getCount().getValue())
                    .collect(Collectors.joining(", "));
            out.print(outgoingArcs);
            out.println(";");

            out.unindent();
            out.println();
        });

        return out.getContent().trim() + "\n";
    }
}

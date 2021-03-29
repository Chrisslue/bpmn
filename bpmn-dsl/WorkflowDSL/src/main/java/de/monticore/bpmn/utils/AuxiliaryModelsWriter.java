package de.monticore.bpmn.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.analysis.graph.GraphDotPrinter;
import de.monticore.bpmn.analysis.graph.GraphUtils;
import de.monticore.bpmn.analysis.lola.LoLaFormulae;
import de.monticore.bpmn.analysis.petrinet.PetriNetAptPrinter;
import de.monticore.bpmn.analysis.petrinet.PetriNetLoLaPrinter;
import de.monticore.bpmn.analysis.petrinet.WorkflowNet;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.jgrapht.Graph;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;
import petrinet.prettyprint.PetrinetDotPrinter;
import petrinet.prettyprint.PetrinetPrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Creates and writes auxiliary models to disk.
 */
public class AuxiliaryModelsWriter {

    private final ASTProcess workflow;

    public AuxiliaryModelsWriter(final ASTProcess workflow) {
        this.workflow = workflow;
    }

    public void print(final Path outputDir) throws IOException {
        if (!GraphVizWriter.isAvailable()) {
            Log.info("0xWFM0003", workflow.getName());
        }
        printGraphModels(outputDir);
        printPetriModels(outputDir);
    }

    private void printGraphModels(final Path outputDir) throws IOException {
        final Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> graph = GraphUtils.processGraphFrom(workflow);

        final String name = workflow.getName();

        // write dot file
        final File dotFile = FilePrinter.from(GraphDotPrinter.print(graph))
                .to(outputDir, name, "graph.dot");

        // write png/svg images
        if (GraphVizWriter.isAvailable()) {
            new GraphVizWriter()
                    .input(dotFile)
                    .outputDir(outputDir)
                    .outputName(Joiners.DOT.join(name, "graph"))
                    .generatePng()
                    .generateSvg();
        }
    }

    // TODO@ZukunftHiwi: split into smaller methods
    private void printPetriModels(final Path outputDir) throws IOException {
        final WorkflowNet workflowNet = WorkflowNet.from(workflow);

        final ASTPetrinet petriNet = workflowNet.getPetriNet();
        final Map<ASTPlace, Long> initialMarking = Maps.newHashMap();
        initialMarking.put(workflowNet.getSource(), 1L);

        final String name = workflow.getName();

        // write pn
        FilePrinter.from(PetrinetPrettyPrinter.print(petriNet))
                .to(outputDir, name, "pn");

        /* skipped for now since ecore incompatibilities when used within other projects
        // write pnml
        final Path pnmlOutPath = outputDir.resolve(Joiners.DOT.join(name, "pnml"));
        try {
            new PetrinetPnmlPrinter(petriNet, initialMarking).export(pnmlOutPath);
        } catch (final Exception e) {
            throw new IOException(e);
        }
         */

        // write apt
        FilePrinter.from(new PetriNetAptPrinter().print(petriNet, initialMarking))
                .to(outputDir, name, "apt");

        // write lola
        FilePrinter.from(new PetriNetLoLaPrinter().print(petriNet, initialMarking))
                .to(outputDir, name, "lola");

        // write lola formulae
        final Path lolaFormulaeDir = outputDir.resolve("formulae");

        FilePrinter.from(LoLaFormulae.completion(workflowNet))
                .to(lolaFormulaeDir, name, "complete.task");

        for (final ASTTransition transition : petriNet.getTransitionList()) {
            FilePrinter.from(LoLaFormulae.dead(transition))
                    .to(lolaFormulaeDir, Joiner.on("-").join(name, transition.getName()), "live.task");
        }
        for (final ASTPlace place : petriNet.getPlaceList()) {
            FilePrinter.from(LoLaFormulae.safe(place))
                    .to(lolaFormulaeDir, Joiner.on("-").join(name, place.getName()), "safe.task");
        }

        // write dot
        final File dotFile = FilePrinter.from(PetrinetDotPrinter.print(petriNet))
                .to(outputDir, name, "petri.dot");

        // write png/svg images
        if (GraphVizWriter.isAvailable()) {
            new GraphVizWriter()
                    .input(dotFile)
                    .outputDir(outputDir)
                    .outputName(Joiners.DOT.join(name, "petri"))
                    .generatePng()
                    .generateSvg();
        }
    }

}

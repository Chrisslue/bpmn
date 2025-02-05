package de.monticore.bpmn.cocos.analysis;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.analysis.lola.LoLaChecker;
import de.monticore.bpmn.analysis.lola.LoLaFormulae;
import de.monticore.bpmn.analysis.petrinet.PetriNetLoLaPrinter;
import de.monticore.bpmn.analysis.petrinet.WorkflowNet;
import de.monticore.bpmn.analysis.petrinet.WorkflowNetConverter;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.utils.FileUtils;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.se_rwth.commons.logging.Log;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import petrinet._ast.ASTPetriNode;

public class ProcessNetIsSound implements WorkflowASTWFProcessCoCo {

  private boolean isSound = true;

  @Override
  public void check(final ASTWFProcess root) {
    // TODO skip ad-hoc sub-processes
    if (!LoLaChecker.isAvailable()) {
      Log.warn(Messages.get("0xWFM8001", root.getName()));
      return;
    }
    if (WorkflowCollectors.toActivities(root).isEmpty()) {
      Log.info(
          "Model does not contain any activities. Skipping soundness check.",
          getClass().getSimpleName());
      return;
    }

    WorkflowNet wfNet = WorkflowNet.from(root);

    boolean soundTranslation =
        wfNet.getWarnings().stream()
            .map(WorkflowNetConverter.Warning::getType)
            .noneMatch(WorkflowNetConverter.Warning.Type.UNSOUND_TRANSLATION::equals);
    if (!soundTranslation) {
      Log.warn(Messages.get("0xWFM8002", root.getName()));
    }

    String lolaNet =
        new PetriNetLoLaPrinter().print(wfNet.getPetriNet(), WorkflowNet.initialMarking(wfNet));
    File lola;
    try {
      lola = FileUtils.createTempFile("lola", "lola");
      FileUtils.writeToFile(lola, lolaNet);
    } catch (IOException e) {
      Log.error("Failed to write LoLa input for process " + root.getName(), e);
      return;
    }

    // a process with non interrupting boundary events will _always_ be unsafe
    boolean hasNoNonInterruptingBoundaryEvents =
        WorkflowCollectors.toEvents(root).stream()
            .noneMatch(event -> event.getSymbol().isBoundary() && event.isNoninterrupt());

    // 1. LIVENESS
    checkLiveness(wfNet, lola, root, wfNet.getMapping());
    if (hasNoNonInterruptingBoundaryEvents) {
      // 2. SAFENESS
      checkSafeness(wfNet, lola, root, wfNet.getMapping());
      // if (!PetriNetUtils.containsCycles(wfNet.getPetriNet())) {
      // 3. OPTION TO COMPLETE
      checkOptionToComplete(wfNet, lola, root, wfNet.getMapping());
      // } else {
      //    Log.warn(Messages.err("0xBPMN77", process.getName()));
      // }
    } else {
      Log.warn(Messages.get("0xWFM8006", root.getName()));
    }

    if (isSound) {
      Log.info("Everything OK!", ProcessNetIsSound.class.getSimpleName());
    }
  }

  /**
   * Checks option to complete property for the given workflow-net {@code wfNet}, i. e. if the final
   * marking can be reached from any reachable marking. Option to complete implies: proper
   * completion property, deadlock-freeness
   *
   * @param wfNet workflow-net to be checked
   */
  private void checkOptionToComplete(
      final WorkflowNet wfNet,
      final File lolaInput,
      final ASTWFProcess root,
      final Map<ASTPetriNode, Set<ASTFlowElement>> mapping) {
    // LoLa: check liveness of the final marking (a marking is live if it is reachable from any
    // reachable marking)
    // LoLa manual - 4.2.16 Soundness of a workflow net

    try {
      boolean result =
          new LoLaChecker()
              .formula(LoLaFormulae.completion(wfNet))
              .input(lolaInput)
              .check()
              .getResult();
      if (!result) {
        isSound = false;
        Log.warn(Messages.get("0xWFM8003", root.getName()), root.get_SourcePositionStart());
      }
    } catch (TimeoutException e) {
      Log.warn("Checking option to complete for " + root.getName() + " took too long. Skipping.");
    } catch (Exception e) {
      Log.error("Unexpected error while checking formula for " + root.getName(), e);
    }
  }

  /**
   * Checks safeness (1-boundedness) property for the given workflow-net {@code wfNet}, i. e. no
   * place can hold more than one token at the same time
   *
   * @param wfNet workflow-net to be checked
   */
  private void checkSafeness(
      final WorkflowNet wfNet,
      final File lolaInput,
      final ASTWFProcess root,
      final Map<ASTPetriNode, Set<ASTFlowElement>> mapping) {
    List<ASTFlowElement> unsafeNodes =
        wfNet.getPlaces().stream()
            .filter(
                place -> {
                  // LoLa manual - 4.2.4 k-boundedness of a place

                  try {
                    boolean result =
                        new LoLaChecker()
                            .formula(LoLaFormulae.safe(place))
                            .input(lolaInput)
                            .check()
                            .getResult();
                    return !result;
                  } catch (TimeoutException e) {
                    Log.warn(
                        "Checking safeness for " + root.getName() + " took too long. Skipping.");
                  } catch (Exception e) {
                    Log.error("Unexpected error while checking safeness for " + root.getName(), e);
                  }
                  return false;
                })
            .map(mapping::get)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

    if (unsafeNodes.size() > 0) {
      isSound = false;
    }
    unsafeNodes.forEach(
        flowNode ->
            Log.warn(
                Messages.get("0xWFM8005", flowNode.getName()), flowNode.get_SourcePositionStart()));
  }

  /**
   * Checks liveness property for the given workflow-net {@code wfNet}, i. e. absence of dead
   * transitions
   *
   * @param wfNet workflow-net to be checked
   */
  private void checkLiveness(
      final WorkflowNet wfNet,
      final File lolaInput,
      final ASTWFProcess root,
      final Map<ASTPetriNode, Set<ASTFlowElement>> mapping) { // no dead transitions
    List<ASTFlowElement> deadNodes =
        wfNet.getTransitions().stream()
            .filter(
                transition -> {
                  // LoLa manual - 4.2.8 Dead transition

                  try {
                    return new LoLaChecker()
                        .formula(LoLaFormulae.dead(transition))
                        .input(lolaInput)
                        .check()
                        .getResult();
                  } catch (TimeoutException e) {
                    Log.warn(
                        "Checking liveness for " + root.getName() + " took too long. Skipping.");
                  } catch (Exception e) {
                    Log.error("Unexpected error while checking safeness for " + root.getName(), e);
                  }
                  return true;
                })
            .map(mapping::get)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .distinct()
            .filter(flowNode -> !(flowNode instanceof ASTWFGateway))
            .collect(Collectors.toList());

    if (deadNodes.size() > 0) {
      isSound = false;
    }
    deadNodes.forEach(
        flowNode ->
            Log.warn(
                Messages.get("0xWFM8004", flowNode.getName()), flowNode.get_SourcePositionStart()));
  }
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.conformance;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class PerformanceWFMBuilder {
  
  Optional<ASTWorkflowCompilationUnit> buildWFM(int size, boolean diff) {
    WorkflowMill.init();
    WorkflowMill.globalScope().clear();
    try {
      Optional<ASTWorkflowCompilationUnit> wf = WorkflowMill.parser().parse_String(String.format(
          "process PerformanceTestWF%s{}", size));
      
      if (wf.isEmpty()) {
        Log.error("Error while parsing the test model.");
        return wf;
      }
      
      Optional<ASTWFEvent> optStart = WorkflowMill.parser().parse_StringWFEvent(
          "  start event Begin;");
      optStart.ifPresent(event -> wf.get().getWFProcess().addFlowElement(event));
      
      Optional<ASTWFEvent> optEnd = WorkflowMill.parser().parse_StringWFEvent("  end event Done;");
      optEnd.ifPresent(event -> wf.get().getWFProcess().addFlowElement(event));
      
      for (int i = 1; i <= size; i++) {
        
        String prefix = "  ";
        String suffix = ";";
        
        String xorBranch1 = "XorSplit%1$s -> F%1$s -> G%1$s -> XorMerge%1$s";
        String xorBranch2 = "XorSplit%1$s -> H%1$s -> XorMerge%1$s";
        
        if (i > 1) {
          Optional<ASTSequenceFlow> opt = WorkflowMill.parser().parse_StringSequenceFlow(String
              .format("  J%s -> A%s;", i - 1, i));
          opt.ifPresent(flow -> wf.get().getWFProcess().addFlowElement(flow));
        }
        else {
          prefix = "  Begin -> ";
          
          if (diff) {
            xorBranch1 = "XorSplit%1$s -> F%1$s -> XorMerge%1$s";
            xorBranch2 = "XorSplit%1$s -> H%1$s -> G%1$s -> XorMerge%1$s";
          }
        }
        
        if (i == size) {
          suffix = " -> Done;";
        }
        
        for (String label : List.of("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")) {
          Optional<ASTWFTask> opt = WorkflowMill.parser().parse_StringWFTask(String.format(
              "  task %s%s;", label, i));
          opt.ifPresent(task -> wf.get().getWFProcess().addFlowElement(task));
        }
        
        Optional<ASTWFGateway> optAndSplit = WorkflowMill.parser().parse_StringWFGateway(String
            .format("  split and AndSplit%s;", i));
        optAndSplit.ifPresent(gateway -> wf.get().getWFProcess().addFlowElement(gateway));
        
        Optional<ASTWFGateway> optAndMerge = WorkflowMill.parser().parse_StringWFGateway(String
            .format("  merge and AndMerge%s;", i));
        optAndMerge.ifPresent(gateway -> wf.get().getWFProcess().addFlowElement(gateway));
        
        Optional<ASTWFGateway> optXorSplit = WorkflowMill.parser().parse_StringWFGateway(String
            .format("  split xor XorSplit%s;", i));
        optXorSplit.ifPresent(gateway -> wf.get().getWFProcess().addFlowElement(gateway));
        
        Optional<ASTWFGateway> optXorMerge = WorkflowMill.parser().parse_StringWFGateway(String
            .format("  merge xor XorMerge%s;", i));
        optXorMerge.ifPresent(gateway -> wf.get().getWFProcess().addFlowElement(gateway));
        
        Optional<ASTWFGateway> optLoopBack = WorkflowMill.parser().parse_StringWFGateway(String
            .format("  merge xor LoopBack%s;", i));
        optLoopBack.ifPresent(gateway -> wf.get().getWFProcess().addFlowElement(gateway));
        
        Optional<ASTWFGateway> optLoop = WorkflowMill.parser().parse_StringWFGateway(String.format(
            "  split xor Loop%s;", i));
        optLoop.ifPresent(gateway -> wf.get().getWFProcess().addFlowElement(gateway));
        
        Optional<ASTSequenceFlow> optMainFlow = WorkflowMill.parser().parse_StringSequenceFlow(
            String.format(prefix + "A%1$s -> LoopBack%1$s -> AndSplit%1$s -> B%1$s -> C%1$s -> "
                + "AndMerge%1$s -> E%1$s -> " + xorBranch1 + " -> I%1$s -> Loop%1$s -> J%1$s"
                + suffix, i));
        optMainFlow.ifPresent(flow -> wf.get().getWFProcess().addFlowElement(flow));
        
        Optional<ASTSequenceFlow> optAndFlow = WorkflowMill.parser().parse_StringSequenceFlow(String
            .format("  AndSplit%1$s -> D%1$s -> AndMerge%1$s;", i));
        optAndFlow.ifPresent(flow -> wf.get().getWFProcess().addFlowElement(flow));
        
        Optional<ASTSequenceFlow> optXorFlow = WorkflowMill.parser().parse_StringSequenceFlow(String
            .format(xorBranch2 + ";", i));
        optXorFlow.ifPresent(flow -> wf.get().getWFProcess().addFlowElement(flow));
        
        Optional<ASTSequenceFlow> optLoopFlow = WorkflowMill.parser().parse_StringSequenceFlow(
            String.format("  Loop%1$s -> LoopBack%1$s;", i));
        optLoopFlow.ifPresent(flow -> wf.get().getWFProcess().addFlowElement(flow));
        
      }
      
      return wf;
      
    }
    catch (IOException e) {
      Log.error("Error while parsing the test model: " + e.getMessage());
    }
    return Optional.empty();
  }
  
}

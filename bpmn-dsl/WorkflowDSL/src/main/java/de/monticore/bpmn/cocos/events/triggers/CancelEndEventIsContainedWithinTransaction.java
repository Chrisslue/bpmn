 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFSubProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 178 Description: A Cancel End Event can only
 * be used within a transaction Sub-Process.
 */
public class CancelEndEventIsContainedWithinTransaction
    implements WorkflowASTWFProcessCoCo, WorkflowASTWFSubProcessCoCo {

  @Override
  public void check(final ASTWFSubProcess subProcess) {
    if (subProcess.getType() == ASTConstantsWorkflow.TRANSACTION) {
      return;
    }

    hasNoCancelEndEventSubProcess(subProcess);
  }

  @Override
  public void check(final ASTWFProcess process) {
    hasNoCancelEndEvent(process);
  }

  private void hasNoCancelEndEvent(final ASTWFProcess container) {
    WorkflowCollectors.toEndEventsLocal(container)
        .forEach(
            event -> {
              WorkflowVisitor2 visitor =
                  new WorkflowVisitor2() {
                    @Override
                    public void endVisit(ASTWFEventTriggerCancel node) {
                      Log.error(
                          Messages.get("0xWFM2022", event.getName()),
                          event.get_SourcePositionStart(),
                          event.get_SourcePositionEnd());
                    }
                  };

              WorkflowTraverser traverser = WorkflowMill.traverser();
              traverser.add4Workflow(visitor);
              event.accept(traverser);
            });
  }

  private void hasNoCancelEndEventSubProcess(final ASTWFSubProcess container) {
    WorkflowCollectors.toEndEventsLocalSubProcess(container)
        .forEach(
            event -> {
              WorkflowVisitor2 visitor =
                  new WorkflowVisitor2() {
                    @Override
                    public void endVisit(ASTWFEventTriggerCancel node) {
                      Log.error(
                          Messages.get("0xWFM2022", event.getName()),
                          event.get_SourcePositionStart(),
                          event.get_SourcePositionEnd());
                    }
                  };

              WorkflowTraverser traverser = WorkflowMill.traverser();
              traverser.add4Workflow(visitor);
              event.accept(traverser);
            });
  }

  private void logErrorIfCancelEndEvent(final ASTWFEvent event) {}
}

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 262 Description: The catch Compensation
 * Intermediate Event MUST only be attached to the boundary of an Activity and, thus, MAY NOT be
 * used in normal flow.
 */
public class CompensateCatchEventIsNotPartOfNormalFlow implements WorkflowASTWFEventCoCo {
  
  @Override
  public void check(final ASTWFEvent event) {
    WorkflowVisitor2 visitor = new WorkflowVisitor2() {
      
      @Override
      public void visit(final ASTWFEventTriggerCompensate trigger) {
        if (event.getSymbol().isBoundary() && !event.isEmptyOutgoings()) {
          Log.error(Messages.get("0xWFM2024", event.getName()), event.get_SourcePositionStart(),
              event.get_SourcePositionEnd());
        }
      }
      
    };
    
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(visitor);
    event.accept(traverser);
  }
  
}

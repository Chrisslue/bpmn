/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFProcessCoCo;
import de.se_rwth.commons.logging.Log;

public class BoundaryEventIsContainedByActivity implements WorkflowASTWFProcessCoCo {
  
  @Override
  public void check(final ASTWFProcess process) {
    WorkflowCollectors.toEventsLocal(process).stream().filter(event -> event instanceof ASTWFEvent)
        .filter(event -> event.getSymbol().isBoundary()).forEach(event -> Log.error(Messages.get(
            "0xWFM1006", event.getName()), event.get_SourcePositionStart(), event
                .get_SourcePositionEnd()));
  }
  
}

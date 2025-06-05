 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.events;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.ASTWFEvent;
import de.monticore.bpmn.workflow._cocos.WorkflowASTWFEventCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 29 Description: Start events can only react
 * to ("catch") a trigger
 */
public class StartEventIsNotThrowing implements WorkflowASTWFEventCoCo {

  @Override
  public void check(final ASTWFEvent node) {
    if (node.isStart() && node.isThrow()) {
      Log.error(
          Messages.get("0xWFM2001", node.getName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd());
    }
  }
}

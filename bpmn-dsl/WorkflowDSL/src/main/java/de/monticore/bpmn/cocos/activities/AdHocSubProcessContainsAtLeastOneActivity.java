package de.monticore.bpmn.cocos.activities;

import de.monticore.bpmn.Messages;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTSubProcess;
import de.monticore.bpmn.workflow._cocos.WorkflowASTSubProcessCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF Page: 181 Description: The list of BPMN elements
 * that MUST be used in an Ad-Hoc Sub-Process: Activity.
 */
public class AdHocSubProcessContainsAtLeastOneActivity implements WorkflowASTSubProcessCoCo {

  @Override
  public void check(final ASTSubProcess subProcess) {
    if (subProcess.isAdHoc() && WorkflowCollectors.toActivitiesLocalSubProcess(subProcess).isEmpty()) {
      Log.error(
          Messages.get("0xWFM4002", subProcess.getName()),
          subProcess.get_SourcePositionStart(),
          subProcess.get_SourcePositionEnd());
    }
  }
}

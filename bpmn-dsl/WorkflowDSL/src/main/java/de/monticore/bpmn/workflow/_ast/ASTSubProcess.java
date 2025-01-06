package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.collectors.WorkflowLocalCollector;
import java.util.List;
import java.util.Optional;
import de.se_rwth.commons.logging.Log;
import java.util.stream.Stream;

public class ASTSubProcess extends ASTSubProcessTOP {

  public boolean isAdHoc() {
    return getType() == ASTConstantsWorkflow.ADHOC;
  }

  public boolean isTransaction() {
    return getType() == ASTConstantsWorkflow.TRANSACTION;
  }

  public List<ASTEvent> getBoundaryEvents() {
    WorkflowLocalCollector<ASTEvent> collector =
        new WorkflowLocalCollector<ASTEvent>(this) {
          @Override
          public void visit(final ASTEvent event) {
            if (event.getSymbol().isBoundary()) {
              select(event);
            }
          }
        };
    return collector.collect(collector);
  }

}

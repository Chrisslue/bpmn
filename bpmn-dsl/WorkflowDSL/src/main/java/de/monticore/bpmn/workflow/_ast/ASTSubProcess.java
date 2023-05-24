package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.collectors.WorkflowLocalCollector;
import java.util.List;

public class ASTSubProcess extends ASTSubProcessTOP {

  public boolean isAdHoc() {
    return getType() == ASTSubProcessType.ADHOC;
  }

  public boolean isTransaction() {
    return getType() == ASTSubProcessType.TRANSACTION;
  }

  public List<ASTEvent> getBoundaryEvents() {
    WorkflowLocalCollector<ASTEvent> collector =
        new WorkflowLocalCollector<ASTEvent>(this) {
          @Override
          public void visit(final ASTEvent event) {
            if (event.isBoundary()) {
              select(event);
            }
          }
        };
    return collector.collect(collector);
  }
}

package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.function.Predicate;

public ASTEvent extends ASTEventTOP {

  public boolean isStart() {
    return getType() == ASTConstantsWorkflow.START;
  }

  public boolean isEnd() {
    return getType() == ASTConstantsWorkflow.END;
  }

  public boolean isIntermediate() {
    return !isStart() && !isEnd();
  }

  class IsIntermediateThrowTrigger implements Predicate<ASTEvent>, WorkflowVisitor2 {
    boolean isThrow;

    @Override
    public boolean test(final ASTEvent event) {
      if (!event.isPresentTrigger()) {
        return true;
      }
      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.add4Workflow(this);
      event.accept(traverser);
      return isThrow;
    }

    @Override
    public void visit(final ASTEventTriggerEscalate trigger) {
      isThrow = true;
    }

    @Override
    public void visit(final ASTEventTriggerCompensate trigger) {
      isThrow = true;
    }
  }

  class IsIntermediateCatchTrigger implements Predicate<ASTEvent>, WorkflowVisitor2 {
    boolean isCatch;

    @Override
    public boolean test(final ASTEvent event) {
      /*            if (event.isBoundary()) {
          return true;
      }*/
      WorkflowTraverser traverser = WorkflowMill.traverser();
      traverser.add4Workflow(this);
      event.accept(traverser);
      return isCatch;
    }

    @Override
    public void visit(final ASTEventTriggerTimer trigger) {
      isCatch = true;
    }

    @Override
    public void visit(final ASTEventTriggerConditional trigger) {
      isCatch = true;
    }

    @Override
    public void visit(final ASTEventTriggerMultiple trigger) {
      if (!trigger.isParallelMultiple()) {
        isCatch = true;
      }
    }
  }
}

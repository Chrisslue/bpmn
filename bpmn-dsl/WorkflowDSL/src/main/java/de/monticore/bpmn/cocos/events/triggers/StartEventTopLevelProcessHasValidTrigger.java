package de.monticore.bpmn.cocos.events.triggers;

import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._cocos.WorkflowASTProcessCoCo;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;

/**
 * Source: https://www.omg.org/spec/BPMN/2.0/PDF
 * Page: 239
 * Description: There are seven (7) types of Start Events for top-level Processes in BPMN (see Table 10.84): None, Message, Timer, Conditional, Signal, Multiple, and Parallel.
 */
public class StartEventTopLevelProcessHasValidTrigger extends AbstractHasValidTriggerCoCo implements WorkflowASTProcessCoCo {

    private static final String ERROR_CODE = "0xWFM2010";

    public StartEventTopLevelProcessHasValidTrigger() {
        super(ERROR_CODE);
    }

    @Override
    public void check(final ASTProcess process) {
        WorkflowCollectors.toEventsLocal(process)
                .stream()
                .filter(ASTEvent::isStart)
                .forEach(this::check);
    }

    private void check(final ASTEvent event) {
        WorkflowVisitor2 visitor = new WorkflowVisitor2() {
            @Override
            public void visit(final ASTEventTriggerEscalate trigger) {
                logError(event);
            }

            @Override
            public void visit(final ASTEventTriggerError trigger) {
                logError(event);
            }

            @Override
            public void visit(final ASTEventTriggerCancel trigger) {
                logError(event);
            }

            @Override
            public void visit(final ASTEventTriggerCompensate trigger) {
                logError(event);
            }

            @Override
            public void visit(final ASTEventTriggerTerminate trigger) {
                logError(event);
            }
        };

        WorkflowTraverser traverser = WorkflowMill.traverser();
        traverser.add4Workflow(visitor);
        event.accept(traverser);
    }

}

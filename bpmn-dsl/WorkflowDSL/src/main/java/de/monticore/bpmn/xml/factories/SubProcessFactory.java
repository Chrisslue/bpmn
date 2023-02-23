package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import jakarta.xml.bind.JAXBElement;
import org.omg.spec.bpmn._20100524.model.*;


public class SubProcessFactory extends XmlFactory<ASTSubProcess, TSubProcess> implements WorkflowVisitor2, WorkflowHandler {

    protected WorkflowTraverser traverser;

    @Override
    public WorkflowTraverser getTraverser() {
        return traverser;
    }

    @Override
    public void setTraverser(WorkflowTraverser traverser) {
        this.traverser = traverser;
    }

    public static JAXBElement<? extends TSubProcess> makeXml(final ASTSubProcess subProcess) {
        return new SubProcessFactory().buildXml(subProcess);
    }

    @Override
    public JAXBElement<? extends TSubProcess> buildXml(final ASTSubProcess subProcess) {
        // visitor to handle concrete type
        subProcess.accept(getTraverser());

        // handle attributes common to all activities
        val.setId(WorkflowXmlUtils.getAsResourceKey(subProcess.getName()));
        val.setName(subProcess.getName());

        if (subProcess.isTriggeredByEvent()) {
            val.setTriggeredByEvent(true);
        }
        if (subProcess.isForCompensation()) {
            val.setIsForCompensation(true);
        }
        if (subProcess.isPresentLoopCharacteristics()) {
            val.setLoopCharacteristics(
                    LoopCharacteristicsFactory.from(subProcess.getLoopCharacteristics()));
        }

        return xml;
    }

    @Override
    public void visit(final ASTSubProcess subProcess) {
        switch (subProcess.getType()) {
            case TRANSACTION: {
                create(factory::createTTransaction, factory::createTransaction);
                break;
            }
            case ADHOC: {
                TAdHocSubProcess ta = create(factory::createTAdHocSubProcess, factory::createAdHocSubProcess);

                if (subProcess.isPresentAdHocCharacteristics()) {
                    TAdHocOrdering ordering = subProcess.getAdHocCharacteristics().isSequential() ?
                            TAdHocOrdering.SEQUENTIAL : TAdHocOrdering.PARALLEL;
                    ta.setOrdering(ordering);
                    if (subProcess.getAdHocCharacteristics().isWaitForRemainingInstances()) {
                        ta.setCancelRemainingInstances(false);
                    }
                    // TODO add condition
                    /*
                    final TFormalExpression tFormalExpression = createFormalExpression(adHoc.getCompletionCondition());
                    tAdHocSubProcess.setCompletionCondition(tFormalExpression);
                    */
                }
                break;
            }
            default: {
                create(factory::createTSubProcess, factory::createSubProcess);
            }
        }
    }

}
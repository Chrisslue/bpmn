package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class AtomicActivityFactory extends XmlFactory<ASTAtomicActivity, TActivity> implements WorkflowVisitor {

    public static JAXBElement<? extends TActivity> makeXml(final ASTAtomicActivity atomicActivity) {
        return new AtomicActivityFactory().buildXml(atomicActivity);
    }

    @Override
    public JAXBElement<? extends TActivity> buildXml(final ASTAtomicActivity activity) {
        // visitor to handle concrete type
        activity.accept(getRealThis());

        // handle attributes common to all activities
        val.setId(WorkflowXmlUtils.getAsResourceKey(activity.getName()));
        val.setName(activity.getName());

        if (activity.isForCompensation()) {
            val.setIsForCompensation(true);
        }
        if (activity.isPresentLoopCharacteristics()) {
            val.setLoopCharacteristics(LoopCharacteristicsFactory.from(activity.getLoopCharacteristics()));
        }

        return xml;
    }

    @Override
    public void visit(final ASTCallActivity callActivity) {
        TCallActivity t = create(factory::createTCallActivity, factory::createCallActivity);

        String key = WorkflowXmlUtils.getAsResourceKey(callActivity.getTemplate());
        t.setCalledElement(new QName(key));

        if (callActivity.getCallActivitySymbol().getIoSpecification().isPresent()) {
            TInputOutputSpecification xmlIoSpec = IOSpecificationFactory.makeXml(
                    callActivity.getCallActivitySymbol().getIoSpecification().get(), callActivity.getName());
            t.setIoSpecification(xmlIoSpec);
        }
    }

    @Override
    public void visit(final ASTTask task) {
        if (task.isPresentType()) {
            switch (task.getType()) {
                case SERVICE: {
                    create(factory::createTServiceTask, factory::createServiceTask);
                    break;
                }
                case SEND: {
                    TSendTask ts = create(factory::createTSendTask, factory::createSendTask);
                    if (task.isPresentTaskTypeAttributes()) {
                        String messageRef = ((ASTTaskSendReceiveTypeAttributes) task.getTaskTypeAttributes()).getMessage();
                        ts.setMessageRef(new QName(messageRef));
                    }
                    break;
                }
                case RECEIVE: {
                    TReceiveTask tr = create(factory::createTReceiveTask, factory::createReceiveTask);

                    if (task.isPresentTaskTypeAttributes()) {
                        String messageRef = ((ASTTaskSendReceiveTypeAttributes) task.getTaskTypeAttributes()).getMessage();
                        tr.setMessageRef(new QName(messageRef));
                    }
                    break;
                }
                case USER: {
                    create(factory::createTUserTask, factory::createUserTask);
                    break;
                }
                case MANUAL: {
                    create(factory::createTManualTask, factory::createManualTask);
                    break;
                }
                case RULE: {
                    create(factory::createTBusinessRuleTask, factory::createBusinessRuleTask);
                    break;
                }
                case SCRIPT: {
                    create(factory::createTScriptTask, factory::createScriptTask);
                    break;
                }
                default: {
                    create(factory::createTTask, factory::createTask);
                    break;
                }
            }
        } else {
            create(factory::createTTask, factory::createTask);
        }

        if (task.getTaskSymbol().getIoSpecification().isPresent()) {
            TInputOutputSpecification xmlIoSpec = IOSpecificationFactory.makeXml(
                    task.getTaskSymbol().getIoSpecification().get(), task.getName());
            val.setIoSpecification(xmlIoSpec);
        }
    }

}

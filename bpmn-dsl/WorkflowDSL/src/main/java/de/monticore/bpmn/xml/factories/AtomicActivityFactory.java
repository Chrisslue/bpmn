package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.ASTAtomicActivity;
import de.monticore.bpmn.workflow._ast.ASTCallActivity;
import de.monticore.bpmn.workflow._ast.ASTTask;
import de.monticore.bpmn.workflow._ast.ASTTaskSendReceiveTypeAttributes;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import jakarta.xml.bind.JAXBElement;
import org.omg.spec.bpmn._20100524.model.TActivity;
import org.omg.spec.bpmn._20100524.model.TCallActivity;
import org.omg.spec.bpmn._20100524.model.TInputOutputSpecification;
import org.omg.spec.bpmn._20100524.model.TReceiveTask;
import org.omg.spec.bpmn._20100524.model.TSendTask;

import javax.xml.namespace.QName;

public class AtomicActivityFactory extends XmlFactory<ASTAtomicActivity, TActivity>
    implements WorkflowVisitor2, WorkflowHandler {

  protected WorkflowTraverser traverser;

  @Override
  public void setTraverser(WorkflowTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public WorkflowTraverser getTraverser() {
    return traverser;
  }

  public static JAXBElement<? extends TActivity> makeXml(final ASTAtomicActivity atomicActivity) {
    return new AtomicActivityFactory().buildXml(atomicActivity);
  }

  @Override
  public JAXBElement<? extends TActivity> buildXml(final ASTAtomicActivity activity) {
    // visitor to handle concrete type
    activity.accept(getTraverser());

    // handle attributes common to all activities
    val.setId(WorkflowXmlUtils.getAsResourceKey(activity.getName()));
    val.setName(activity.getName());

    if (activity.isForCompensation()) {
      val.setIsForCompensation(true);
    }
    if (activity.isPresentLoopCharacteristics()) {
      val.setLoopCharacteristics(
          LoopCharacteristicsFactory.from(activity.getLoopCharacteristics()));
    }

    return xml;
  }

  @Override
  public void visit(final ASTCallActivity callActivity) {
    TCallActivity t = create(factory::createTCallActivity, factory::createCallActivity);

    String key = WorkflowXmlUtils.getAsResourceKey(callActivity.getTemplate());
    t.setCalledElement(new QName(key));

    if (callActivity.getSymbol().isPresentIOSpecification()) {
      TInputOutputSpecification xmlIoSpec =
          IOSpecificationFactory.makeXml(
              callActivity.getSymbol().getIOSpecification(), callActivity.getName());
      t.setIoSpecification(xmlIoSpec);
    }
  }

  @Override
  public void visit(final ASTTask task) {
    if (task.isPresentType()) {
      switch (task.getType()) {
        case SERVICE:
          {
            create(factory::createTServiceTask, factory::createServiceTask);
            break;
          }
        case SEND:
          {
            TSendTask ts = create(factory::createTSendTask, factory::createSendTask);
            if (task.isPresentTaskTypeAttributes()) {
              String messageRef =
                  ((ASTTaskSendReceiveTypeAttributes) task.getTaskTypeAttributes()).getMessage();
              ts.setMessageRef(new QName(messageRef));
            }
            break;
          }
        case RECEIVE:
          {
            TReceiveTask tr = create(factory::createTReceiveTask, factory::createReceiveTask);

            if (task.isPresentTaskTypeAttributes()) {
              String messageRef =
                  ((ASTTaskSendReceiveTypeAttributes) task.getTaskTypeAttributes()).getMessage();
              tr.setMessageRef(new QName(messageRef));
            }
            break;
          }
        case USER:
          {
            create(factory::createTUserTask, factory::createUserTask);
            break;
          }
        case MANUAL:
          {
            create(factory::createTManualTask, factory::createManualTask);
            break;
          }
        case RULE:
          {
            create(factory::createTBusinessRuleTask, factory::createBusinessRuleTask);
            break;
          }
        case SCRIPT:
          {
            create(factory::createTScriptTask, factory::createScriptTask);
            break;
          }
        default:
          {
            create(factory::createTTask, factory::createTask);
            break;
          }
      }
    } else {
      create(factory::createTTask, factory::createTask);
    }

    if (task.getSymbol().isPresentIOSpecification()) {
      TInputOutputSpecification xmlIoSpec =
          IOSpecificationFactory.makeXml(
              task.getSymbol().getIOSpecification(), task.getName());
      val.setIoSpecification(xmlIoSpec);
    }
  }
}

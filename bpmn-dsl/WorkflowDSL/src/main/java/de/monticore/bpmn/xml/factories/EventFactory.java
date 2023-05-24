package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import jakarta.xml.bind.JAXBElement;
import java.util.List;
import javax.xml.namespace.QName;
import org.omg.spec.bpmn._20100524.model.*;

public class EventFactory extends XmlFactory<ASTEvent, TEvent>
    implements WorkflowVisitor2, WorkflowHandler {

  protected WorkflowTraverser traverser;

  @Override
  public WorkflowTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(WorkflowTraverser traverser) {
    this.traverser = traverser;
  }

  private boolean isParallelMultiple;

  public static JAXBElement<? extends TEvent> makeXml(final ASTEvent event) {
    return new EventFactory().buildXml(event);
  }

  @Override
  public JAXBElement<? extends TEvent> buildXml(final ASTEvent event) {
    event.accept(getTraverser());

    return xml;
  }

  @Override
  public void endVisit(final ASTEventTriggerMultiple multipleTrigger) {
    isParallelMultiple = multipleTrigger.isParallelMultiple();
  }

  @Override
  public void endVisit(final ASTNamedEvent event) {
    makeXmlEvent(event);
  }

  @Override
  public void endVisit(final ASTInlineEvent event) {
    makeXmlEvent(event);
  }

  protected void makeXmlEvent(final ASTEvent event) {
    if (event.isCatch()) {
      final TCatchEvent t;
      if (event.isStart()) {
        TStartEvent ts = create(factory::createTStartEvent, factory::createStartEvent);
        t = ts;

        if (event.isNonInterrupt()) {
          ts.setIsInterrupting(false);
        }
      } else if (event.isBoundary()) {
        TBoundaryEvent tb = create(factory::createTBoundaryEvent, factory::createBoundaryEvent);
        t = tb;

        if (event.isNonInterrupt()) {
          tb.setCancelActivity(false);
        }
        tb.setAttachedToRef(new QName(event.getEnclosingScope().getName()));

      } else {
        t = create(factory::createTIntermediateCatchEvent, factory::createIntermediateCatchEvent);
      }
      if (event.isPresentTrigger()) {
        List<JAXBElement<? extends TEventDefinition>> jaxbElements =
            buildXmlTriggers(event.getTrigger());
        t.getEventDefinition().addAll(jaxbElements);
      }
      t.setParallelMultiple(isParallelMultiple);
    } else {
      final TThrowEvent t;
      if (event.isEnd()) {
        t = create(factory::createTEndEvent, factory::createEndEvent);
      } else {
        t = create(factory::createTIntermediateThrowEvent, factory::createIntermediateThrowEvent);
      }
      if (event.isPresentTrigger()) {
        List<JAXBElement<? extends TEventDefinition>> jaxbElements =
            buildXmlTriggers(event.getTrigger());
        t.getEventDefinition().addAll(jaxbElements);
      }
    }

    val.setId(WorkflowXmlUtils.getAsResourceKey(event.getName()));
    val.setName(event.getName());
  }

  private List<JAXBElement<? extends TEventDefinition>> buildXmlTriggers(
      final ASTEventTrigger trigger) {
    return EventTriggerFactory.makeXml(trigger);
  }
}

package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.List;

public class EventFactory extends XmlFactory<ASTEvent, TEvent> implements WorkflowVisitor {

    private boolean isParallelMultiple;

    public static JAXBElement<? extends TEvent> makeXml(final ASTEvent event) {
        return new EventFactory().buildXml(event);
    }

    @Override
    public JAXBElement<? extends TEvent> buildXml(final ASTEvent event) {
        event.accept(getRealThis());

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
                tb.setAttachedToRef(new QName(event.getEnclosingScope().getName().get()));

            } else {
                t = create(factory::createTIntermediateCatchEvent, factory::createIntermediateCatchEvent);
            }
            event.getTriggerOpt().map(this::buildXmlTriggers)
                    .ifPresent(triggers -> t.getEventDefinition().addAll(triggers));
            t.setParallelMultiple(isParallelMultiple);
        } else {
            final TThrowEvent t;
            if (event.isEnd()) {
                t = create(factory::createTEndEvent, factory::createEndEvent);
            } else {
                t = create(factory::createTIntermediateThrowEvent, factory::createIntermediateThrowEvent);
            }
            event.getTriggerOpt().map(this::buildXmlTriggers)
                    .ifPresent(triggers -> t.getEventDefinition().addAll(triggers));
        }

        val.setId(WorkflowXmlUtils.getAsResourceKey(event.getName()));
        val.setName(event.getName());
    }

    private List<JAXBElement<? extends TEventDefinition>> buildXmlTriggers(final ASTEventTrigger trigger) {
        return EventTriggerFactory.makeXml(trigger);
    }

}

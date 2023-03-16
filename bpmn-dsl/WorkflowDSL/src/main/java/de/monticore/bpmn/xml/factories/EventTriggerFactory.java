package de.monticore.bpmn.xml.factories;

import com.google.common.collect.Lists;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.expressions.timeexpressions._ast.*;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import jakarta.xml.bind.JAXBElement;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.namespace.QName;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EventTriggerFactory implements WorkflowVisitor2, WorkflowHandler {

    protected WorkflowTraverser traverser;

    @Override
    public WorkflowTraverser getTraverser() {
        return traverser;
    }

    @Override
    public void setTraverser(WorkflowTraverser traverser) {
        this.traverser = traverser;
    }

    private static final ObjectFactory factory = new ObjectFactory();

    private List<JAXBElement<? extends TEventDefinition>> xmlTriggers = Lists.newArrayList();

    public static List<JAXBElement<? extends TEventDefinition>> makeXml(final ASTEventTrigger trigger) {
        return new EventTriggerFactory().buildXml(trigger);
    }

    public  List<JAXBElement<? extends TEventDefinition>> buildXml(final ASTEventTrigger trigger) {
        trigger.accept(getTraverser());

        return xmlTriggers;
    }

    @Override
    public void visit(final ASTEventTriggerCancel trigger) {
        createTrigger(factory::createTCancelEventDefinition, factory::createCancelEventDefinition);
    }

    @Override
    public void visit(final ASTEventTriggerCompensate trigger) {
        TCompensateEventDefinition t = createTrigger(factory::createTCompensateEventDefinition, factory::createCompensateEventDefinition);
        if (trigger.isAsync()) {
            t.setWaitForCompletion(false);
        }
        if(trigger.isPresentActivity()){
            t.setActivityRef(new QName(trigger.getActivity()));
        }
    }

    @Override
    public void visit(final ASTEventTriggerConditional trigger) {
        createTrigger(factory::createTConditionalEventDefinition, factory::createConditionalEventDefinition);
        // TODO condition
        /*
        final TFormalExpression tFormalExpression = createFormalExpression(conditionalDefinition.getCondition());
        tConditionalEventDefinition.setCondition(tFormalExpression);
        */
    }

    @Override
    public void visit(final ASTEventTriggerError trigger) {
        createTrigger(factory::createTErrorEventDefinition, factory::createErrorEventDefinition);
        // TODO error ref
    }

    @Override
    public void visit(final ASTEventTriggerEscalate trigger) {
        createTrigger(factory::createTEscalationEventDefinition, factory::createEscalationEventDefinition);
        // TODO escalation ref
    }

    @Override
    public void visit(final ASTEventTriggerMessage trigger) {
        createTrigger(factory::createTMessageEventDefinition, factory::createMessageEventDefinition);
        // TODO message ref
    }

    @Override
    public void visit(final ASTEventTriggerSignal trigger) {
        createTrigger(factory::createTSignalEventDefinition, factory::createSignalEventDefinition);
        // TODO signal ref
    }

    @Override
    public void visit(final ASTEventTriggerTerminate trigger) {
        createTrigger(factory::createTTerminateEventDefinition, factory::createTerminateEventDefinition);
    }

    // TODO remove casting
    @Override
    public void visit(final ASTEventTriggerTimer trigger) {
        TTimerEventDefinition tDefinition = createTrigger(factory::createTTimerEventDefinition, factory::createTimerEventDefinition);

        ASTTimeExpression timer = ((ASTTimerExpression) trigger.getExpression()).getTimeExpression();
        if (timer instanceof ASTDateExpr) {
            ASTDateExpr dateExpr = (ASTDateExpr) timer;
            buildTimerExpression(tDefinition::setTimeDate, dateExpr.printISO8601());
        } else if (timer instanceof ASTTimeExpr) {
            ASTTimeExpr timeExpr = (ASTTimeExpr) timer;
            buildTimerExpression(tDefinition::setTimeDate, timeExpr.printISO8601());
        } else if (timer instanceof ASTAfterExpr) {
            ASTAfterExpr afterExpr = (ASTAfterExpr) timer;
            buildTimerExpression(tDefinition::setTimeDuration, afterExpr.printISO8601());
        } else if (timer instanceof ASTEveryExpr) {
            ASTEveryExpr everyExpr = (ASTEveryExpr) timer;
            buildTimerExpression(tDefinition::setTimeCycle, everyExpr.printISO8601());
        } else if (timer instanceof ASTCronExpr) {
            ASTCronExpr cronExpr = (ASTCronExpr) timer;
            buildTimerExpression(tDefinition::setTimeCycle, cronExpr.printCron());
        }
    }

    protected void buildTimerExpression(final Consumer<TFormalExpression> target, final String content) {
        TFormalExpression t = CommonFactory.buildXmlFormalExpression();
        t.getContent().add(content);
        target.accept(t);
    }

    protected <V extends TEventDefinition> V createTrigger(Supplier<V> supplier, Function<V, JAXBElement<V>> xmlFactory) {
        V value = supplier.get();
        xmlTriggers.add(xmlFactory.apply(value));
        return value;
    }

}

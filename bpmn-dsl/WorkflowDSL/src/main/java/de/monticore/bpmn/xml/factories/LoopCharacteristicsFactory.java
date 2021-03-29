package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import org.omg.spec.bpmn._20100524.model.TLoopCharacteristics;
import org.omg.spec.bpmn._20100524.model.TMultiInstanceFlowCondition;
import org.omg.spec.bpmn._20100524.model.TMultiInstanceLoopCharacteristics;
import org.omg.spec.bpmn._20100524.model.TStandardLoopCharacteristics;

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;

public class LoopCharacteristicsFactory extends XmlFactory<ASTLoopCharacteristics, TLoopCharacteristics> implements WorkflowVisitor {

    public static JAXBElement<? extends TLoopCharacteristics> from(final ASTLoopCharacteristics loopCharacteristics) {
        return new LoopCharacteristicsFactory().buildXml(loopCharacteristics);
    }

    @Override
    public JAXBElement<? extends TLoopCharacteristics> buildXml(final ASTLoopCharacteristics loopCharacteristics) {
        loopCharacteristics.accept(getRealThis());

        return xml;
    }

    @Override
    public void visit(final ASTStandardLoop loop) {
        TStandardLoopCharacteristics t = create(factory::createTStandardLoopCharacteristics, factory::createStandardLoopCharacteristics);
        if (loop.isTestBefore()) {
            t.setTestBefore(true);
        }
        if (loop.isPresentLoopMaximum()) {
            BigInteger loopMaximum = BigInteger.valueOf(loop.getLoopMaximum().getValue());
            t.setLoopMaximum(loopMaximum);
        }

        // TODO add condition
        /*
        final TFormalExpression tFormalExpression = createFormalExpression(loop.getLoopCondition());
        tLoopCharacteristics.setLoopCondition(tFormalExpression);
        */
    }

    @Override
    public void visit(final ASTMILoop loop) {
        TMultiInstanceLoopCharacteristics t = create(factory::createTMultiInstanceLoopCharacteristics, factory::createMultiInstanceLoopCharacteristics);

        if (loop.isSequential()) {
            t.setIsSequential(true);
        }
        if (loop.isPresentCompletionCondition()) {
            // TODO add condition
            /*
            final TFormalExpression tFormalExpression = createFormalExpression(loop.getCompletionCondition());
            tLoopCharacteristics.setCompletionCondition(tFormalExpression);
            */
        }
        ASTLoopCardinality loopCardinality = loop.getLoopCardinality();
        if (loopCardinality.isPresentCount()) {
            int countExpression = loopCardinality.getCount().getValue();
            // TODO add condition
            /*
                final TFormalExpression tFormalExpression = createFormalExpression(countExpression);
                tLoopCharacteristics.setLoopCardinality(tFormalExpression);
             */
        }
        if (loopCardinality.isPresentExpression()) {
            // TODO add condition
            /*
                final TFormalExpression tFormalExpression = createFormalExpression(loopCardinality.getExpression());
                tLoopCharacteristics.setLoopCardinality(tFormalExpression);
             */
        }

        if (loop.isPresentImplicitBehavior()) {
            ASTMIImplicitEventBehavior behavior = loop.getImplicitBehavior();
            if (behavior.isNone()) {
                t.setBehavior(TMultiInstanceFlowCondition.ALL);
            }
            if (behavior.isFirst()) {
                t.setBehavior(TMultiInstanceFlowCondition.ONE);
                // TODO final TImplicitThrowEvent tImplicitThrowEvent = createImplicitThrowEvent();
                // TODO tLoopCharacteristics.setOneBehaviorEventRef();
            }
            if (behavior.isEvery()) {
                t.setBehavior(TMultiInstanceFlowCondition.NONE);
                // TODO final TImplicitThrowEvent tImplicitThrowEvent = createImplicitThrowEvent();
                // TODO tLoopCharacteristics.setNoneBehaviorEventRef();
            }
            if (behavior.isPresentComplexCondition()) {
                t.setBehavior(TMultiInstanceFlowCondition.COMPLEX);
                // TODO add condition
                /*
                final TFormalExpression tFormalExpression = createFormalExpression(behavior.getComplexCondition());
                final TComplexBehaviorDefinition tComplexBehavior = factory.createTComplexBehaviorDefinition();
                tComplexBehavior.setCondition(tFormalExpression);
                */
                // TODO final TImplicitThrowEvent tImplicitThrowEvent = createImplicitThrowEvent()
                // TODO tComplexBehavior.setEvent(tImplicitThrowEvent);
            }
            // TODO loopDataInputRef
            // TODO loopDataOutputRef
            // TODO inputDataItem
            // TODO outputDataItem
        }
    }

}

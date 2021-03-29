package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class GatewayFactory extends XmlFactory<ASTGateway, TGateway> implements WorkflowVisitor {

    public static JAXBElement<? extends TGateway> makeXml(final ASTGateway gateway) {
        return new GatewayFactory().buildXml(gateway);
    }

    @Override
    public JAXBElement<? extends TGateway> buildXml(final ASTGateway gateway) {
        gateway.accept(getRealThis());

        return xml;
    }

    @Override
    public void visit(final ASTNamedGateway gateway) {
        handleGateway(gateway);
    }

    @Override
    public void visit(final ASTInlineGateway gateway) {
        handleGateway(gateway);
    }

    private void handleGateway(final ASTGateway gateway) {
        final ASTGatewayType type = gateway.getType();
        if (type.isExclusive()) {
            TExclusiveGateway tg = create(factory::createTExclusiveGateway, factory::createExclusiveGateway);
/*            gateway.streamOutgoings()
                    .filter(SequenceFlow::isDefault)
                    .findFirst()
                    .ifPresent(flow -> tg.setDefault(new QName(flow.getName())));*/
        } else if (type.isInclusive()) {
            create(factory::createTInclusiveGateway, factory::createInclusiveGateway);
        } else if (type.isParallel()) {
            create(factory::createTParallelGateway, factory::createParallelGateway);
        } else if (type.isExclusiveEventBased() || type.isParallelEventBased()) {
            TEventBasedGateway tg = create(factory::createTEventBasedGateway, factory::createEventBasedGateway);
            if (gateway.isEmptyIncomings()) {
                tg.setInstantiate(true);
            }
            tg.setEventGatewayType(type.isExclusiveEventBased() ? TEventBasedGatewayType.EXCLUSIVE : TEventBasedGatewayType.PARALLEL);
        } else if (type.isComplex()) {
            TComplexGateway tg = create(factory::createTComplexGateway, factory::createComplexGateway);
            // TODO handle complex gateway condition
        } else {
            create(factory::createTExclusiveGateway, factory::createExclusiveGateway);
        }

        switch (gateway.getDirection()) {
            case SPLIT:
                val.setGatewayDirection(TGatewayDirection.DIVERGING);
                break;
            case MERGE:
                val.setGatewayDirection(TGatewayDirection.CONVERGING);
                break;
        }

        val.setId(WorkflowXmlUtils.getAsResourceKey(gateway.getName()));
        val.setName(gateway.getName());
    }

}
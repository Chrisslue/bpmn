package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.TypesHelper;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.xml.WorkflowXmlUtils;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.function.Function;
import java.util.function.Supplier;

public class CommonFactory {

    private static final ObjectFactory factory = new ObjectFactory();

    public static JAXBElement<TProcess> makeXmlProcess(final ASTProcess process) {
        JAXBElement<TProcess> xmlNode = create(factory::createTProcess, factory::createProcess);
        TProcess value = xmlNode.getValue();
        value.setId(WorkflowXmlUtils.getAsResourceKey(process.getName()));
        value.setName(process.getName());
        value.setIsExecutable(true);

        // TODO add IO

        return xmlNode;
    }

    public static JAXBElement<TDefinitions> makeDefinitions(final ASTWorkflowCompilationUnit unit, final String name, final String targetNamespace) {
        JAXBElement<TDefinitions> xmlNode = create(factory::createTDefinitions, factory::createDefinitions);
        TDefinitions value = xmlNode.getValue();
        value.setTargetNamespace(targetNamespace);
        value.setId(WorkflowXmlUtils.getAsResourceKey(name));
        value.setName(name);

        return xmlNode;
    }

    public static TLaneSet makeXmlLaneSet() {
        TLaneSet value = factory.createTLaneSet();
        return value;
    }

    public static TLane makeXmlLane(final ASTLane lane) {
        TLane value = factory.createTLane();
        value.setId(WorkflowXmlUtils.getAsResourceKey("Lane" + lane.getName()));
        value.setName(lane.getName());

        return value;
    }

    public static JAXBElement<Object> makeXmlLaneFlowNodeRef(final TFlowNode xmlNode) {
        return factory.createTLaneFlowNodeRef(xmlNode);
    }

    public static JAXBElement<TSequenceFlow> makeXmlSequenceFlow(final SequenceFlow flow, final TFlowNode source, final TFlowNode target) {
        JAXBElement<TSequenceFlow> xmlNode = create(factory::createTSequenceFlow, factory::createSequenceFlow);
        TSequenceFlow value = xmlNode.getValue();
        value.setId(WorkflowXmlUtils.getAsResourceKey(flow.getName()));
        value.setIsImmediate(true);

        value.setSourceRef(source);
        value.setTargetRef(target);

        return xmlNode;
    }

    public static JAXBElement<TAssociation> makeXmlAssociation(final QName source, final QName target) {
        JAXBElement<TAssociation> xmlNode = create(factory::createTAssociation, factory::createAssociation);
        TAssociation value = factory.createTAssociation();
        value.setSourceRef(source);
        value.setTargetRef(target);

        return xmlNode;
    }

    public static QName makeQName(final String name) {
        return new QName(name);
    }

    public static JAXBElement<TDataObject> makeXmlDataObject(final ASTDataObject dataObject) {
        JAXBElement<TDataObject> xmlNode = create(factory::createTDataObject, factory::createDataObject);
        TDataObject value = xmlNode.getValue();
        // final String type = TypesPrinter.printType(dataObject.getType());
        final String type = dataObject.getDataObjectSymbol().getTypeSymbolRef().getFullName();
        value.setName(dataObject.getName());
        value.setIsCollection(TypesHelper.isCollection(type));
        value.setItemSubjectRef(new QName(type));

        return xmlNode;
    }

    public static TFormalExpression buildXmlFormalExpression() {
        return factory.createTFormalExpression();
    }

    public static TExtensionElements makeXmlExtensions() {
        return factory.createTExtensionElements();
    }

    private static <V> JAXBElement<V> create(Supplier<V> supplier, Function<V, JAXBElement<V>> xmlFactory) {
        V value = supplier.get();
        return xmlFactory.apply(value);
    }

}
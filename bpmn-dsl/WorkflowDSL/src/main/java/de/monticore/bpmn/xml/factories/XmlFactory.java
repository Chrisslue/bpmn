package de.monticore.bpmn.xml.factories;

import de.monticore.bpmn.workflow._ast.ASTWorkflowNode;
import jakarta.xml.bind.JAXBElement;
import org.omg.spec.bpmn._20100524.model.ObjectFactory;

import java.util.function.Function;
import java.util.function.Supplier;

abstract class XmlFactory<T extends ASTWorkflowNode, U> {

    protected static final ObjectFactory factory = new ObjectFactory();

    protected JAXBElement<? extends U> xml;
    protected U val;

    abstract JAXBElement<? extends U> buildXml(final T node);

    protected <V extends U> V create(Supplier<V> supplier, Function<V, JAXBElement<? extends V>> xmlFactory) {
        V value = supplier.get();
        val = value;
        xml = xmlFactory.apply(value);
        return value;
    }

}

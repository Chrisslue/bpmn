package de.monticore.bpmn.xml;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.utils.FileUtils;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import org.junit.jupiter.api.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;

class XmlSerializationTest extends AbstractTest {

    @Test
    void serialize() throws IOException, JAXBException {
        ASTWorkflowCompilationUnit unit = loadModel("de.monticore.bpmn.xml.Example");

        File xmlFile = FileUtils.createTempFile("test", "bpmn");

        new WorkflowXmlSerializer(unit, new WorkflowXmlSerializerVisitor(unit.getProcess()))
                .makeXml()
                .writeToFile(xmlFile);
    }

}

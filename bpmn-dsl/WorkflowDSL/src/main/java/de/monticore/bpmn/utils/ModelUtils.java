package de.monticore.bpmn.utils;

import de.monticore.bpmn.NamesHelper;
import de.monticore.bpmn.workflow._ast.ASTProcess;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._symboltable.WorkflowLanguage;
import de.monticore.bpmn.xml.WorkflowXmlSerializer;
import de.monticore.bpmn.xml.WorkflowXmlSerializerVisitor;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utilities for locating and writing models.
 */
public class ModelUtils {

    public static final String BPMN_XML_FILE_EXTENSION = "bpmn";

    public static ModelCoordinate getCoordinate(final ModelPath modelPath, final String qualifiedModelName) {
        return modelPath.resolveModel(getCoordinate(qualifiedModelName));
    }

    public static ModelCoordinate getCoordinate(final String qualifiedModelName) {
        Path qualifiedPath = Paths.get(Names.getPathFromQualifiedName(qualifiedModelName))
                .resolve(Names.getSimpleName(qualifiedModelName) + "." + WorkflowLanguage.FILE_ENDING);

        return ModelCoordinates.createQualifiedCoordinate(qualifiedPath);
    }

    public static void exportXml(final File outputDir, final ASTWorkflowCompilationUnit unit) throws JAXBException {
        String xmlFileName = Joiners.DOT.join(NamesHelper.getXmlFileName(unit), BPMN_XML_FILE_EXTENSION);
        File xmlFile = new File(outputDir, xmlFileName);
        // ensure that output directory exist
        outputDir.mkdirs();

        ASTProcess process = unit.getProcess();

        Log.debug("Serializing WFM ...", ModelUtils.class.getSimpleName());
        new WorkflowXmlSerializer(unit, new WorkflowXmlSerializerVisitor(process))
                .makeXml()
                .writeToFile(xmlFile);
        Log.debug("Done.", ModelUtils.class.getSimpleName());
    }

}

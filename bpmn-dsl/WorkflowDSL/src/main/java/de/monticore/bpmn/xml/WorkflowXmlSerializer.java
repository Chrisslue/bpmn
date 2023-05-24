package de.monticore.bpmn.xml;

import static de.monticore.bpmn.xml.WorkflowXmlUtils.getAsResourceKey;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.se_rwth.commons.logging.Log;
import jakarta.xml.bind.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLEventWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.omg.spec.bpmn._20100524.model.TDefinitions;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/** Exports a BPMN model into the BPMN 2.0 XML exchange format. */
public class WorkflowXmlSerializer {

  private static final String DEFINITIONS_PREFIX = "Definitions";

  // Do not remove the leading slash, it will break everything.
  private static final String BPMN_SCHEMA_FILE = "/BPMN20.xsd";

  private final WorkflowXmlSerializerVisitor serializer;

  private final ASTWorkflowCompilationUnit unit;

  private JAXBElement<TDefinitions> xmlContentTree;

  public WorkflowXmlSerializer(
      final ASTWorkflowCompilationUnit unit, final WorkflowXmlSerializerVisitor serializer) {
    this.serializer = serializer;
    this.unit = unit;
  }

  /**
   * Creates the XML content tree.
   *
   * @return this
   */
  public WorkflowXmlSerializer makeXml() {
    String targetNamespace = unit.getPackageName().orElse("");
    String name = getAsResourceKey(DEFINITIONS_PREFIX + unit.getProcess().getName());

    serializer.makeXml();
    // TODO
    //        xmlContentTree = CommonFactory.makeDefinitions(unit, name, targetNamespace);

    //        TDefinitions definitions = xmlContentTree.getValue();
    //        definitions.getRootElement().addAll(serializer.getRootElements());

    return this;
  }

  /**
   * Returns the XML content tree.
   *
   * @return the XML content tree
   */
  public JAXBElement<TDefinitions> getResult() {
    if (null == xmlContentTree) {
      throw new IllegalStateException();
    }
    return xmlContentTree;
  }

  /**
   * Validates the XML content tree against the XML schema definition.
   *
   * @return this
   * @throws JAXBException
   */
  public WorkflowXmlSerializer validate() throws JAXBException {
    List<Class<?>> classesToBeBound = Lists.newArrayList(TDefinitions.class);
    classesToBeBound.addAll(getAdditionalClassesToBeBound());

    // get JAXB context
    JAXBContext ctx = JAXBContext.newInstance(classesToBeBound.toArray(new Class<?>[0]));

    // set up marshaller
    Marshaller m = ctx.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    getAdditionalMarshallerProperties()
        .forEach(
            (key, value) -> {
              try {
                m.setProperty(key, value);
              } catch (PropertyException e) {
                e.printStackTrace();
              }
            });

    // set up schema validator
    SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    try {
      URL schemaUrl = getClass().getResource(BPMN_SCHEMA_FILE);
      if (schemaUrl != null) {
        Schema schema = schemaFactory.newSchema(schemaUrl);
        m.setSchema(schema);
      } else {
        Log.warn("Unable to load BPMN schema files. Schema validation will be skipped.");
      }
    } catch (SAXException e) {
      Log.warn("Unable to load BPMN schema files. Schema validation will be skipped.", e);
    }

    m.marshal(xmlContentTree, (XMLEventWriter) new DefaultHandler());

    return this;
  }

  /**
   * Marshals the XML content tree into the output file.
   *
   * @param file the output file
   * @return this
   * @throws JAXBException
   */
  public WorkflowXmlSerializer writeToFile(final File file) throws JAXBException {
    List<Class<?>> classesToBeBound = Lists.newArrayList(TDefinitions.class);
    classesToBeBound.addAll(getAdditionalClassesToBeBound());

    // get JAXB context
    JAXBContext ctx = JAXBContext.newInstance(classesToBeBound.toArray(new Class<?>[0]));

    // set up marshaller
    Marshaller m = ctx.createMarshaller();
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
    getAdditionalMarshallerProperties()
        .forEach(
            (key, value) -> {
              try {
                m.setProperty(key, value);
              } catch (PropertyException e) {
                e.printStackTrace();
              }
            });

    // marshal to XML file
    m.marshal(xmlContentTree, file);

    return this;
  }

  /**
   * Returns additional classes to registered at the marshaller.
   *
   * @return the classes to be registered
   */
  protected List<Class<?>> getAdditionalClassesToBeBound() {
    return Lists.newArrayList();
  }

  /**
   * Returns any additional marshaller properties.
   *
   * @return the additional marshaller properties
   */
  protected Map<String, Object> getAdditionalMarshallerProperties() {
    return Maps.newHashMap();
  }
}

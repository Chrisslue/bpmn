/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.analysis.graph;

import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import java.io.IOException;
import java.io.StringWriter;
import org.jgrapht.Graph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

/** Prints the control flow graph into the dot file format. */
public class GraphDotPrinter {
  
  public static String print(final Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> graph)
      throws IOException {
    final GraphExporter<ASTFlowElement, EndpointPair<ASTFlowElement>> exporter = new DOTExporter<>(
        ASTFlowElement::getName, null, null);
    // write dot file
    final StringWriter writer = new StringWriter();
    try {
      exporter.exportGraph(graph, writer);
    }
    catch (ExportException e) {
      throw new IOException(e);
    }
    
    return writer.toString();
  }
  
}

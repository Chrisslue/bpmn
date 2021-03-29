package de.monticore.bpmn.analysis.graph;

import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import org.jgrapht.Graph;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphExporter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Prints the control flow graph into the dot file format.
 */
public class GraphDotPrinter {

    public static String print(final Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> graph) throws IOException {
        final GraphExporter<ASTFlowNode, EndpointPair<ASTFlowNode>> exporter =
                new DOTExporter<>(ASTFlowNode::getName, null, null);
        // write dot file
        final StringWriter writer = new StringWriter();
        try {
            exporter.exportGraph(graph, writer);
        } catch (ExportException e) {
            throw new IOException(e);
        }

        return writer.toString();
    }
}

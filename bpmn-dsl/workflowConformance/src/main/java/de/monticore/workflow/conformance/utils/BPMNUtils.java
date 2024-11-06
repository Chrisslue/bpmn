package de.monticore.workflow.conformance.utils;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import de.monticore.bpmn.wf2lts.collector.StartEventCollector;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.ctl.CTLGraph;
import de.monticore.workflow.conformance.datastructure.ctl.TokenController;
import javax.swing.*;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

public class BPMNUtils {
  public static ASTEvent getStartEvent(ASTWorkflowCompilationUnit ast) {

    WorkflowTraverser traverser = WorkflowMill.traverser();
    StartEventCollector startEventCollector = new StartEventCollector(traverser);
    for (ASTFlowElement astFlowElement : ast.getProcess().getFlowElementList()) {
      astFlowElement.accept(traverser);
    }
    var startEvents = startEventCollector.getEvents();
    if (startEvents.size() != 1) {
      throw new IllegalArgumentException("Workflow had more than one start element");
    }
    return startEvents.get(0);
  }

  public static void visualize(CTLGraph graph) {
    System.out.println("Start Visualizing");
    int i = 0;
    // Create JGraphXAdapter for visualization
    JGraphXAdapter<TokenController, DefaultEdge> jgxAdapter = new JGraphXAdapter<>(graph.graph);

    // Create JGraphXAdapter for visualization

    // Apply a layout for better visualization
    mxGraphLayout layout = new mxHierarchicalLayout(jgxAdapter);
    layout.execute(jgxAdapter.getDefaultParent());

    // Display in a JFrame
    JFrame frame = new JFrame("JGraphT Visualization with JGraphX");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    // Disable adding new edges
    jgxAdapter.setAllowDanglingEdges(false);

    mxGraphComponent graphComponent = new mxGraphComponent(jgxAdapter);
    frame.getContentPane().add(graphComponent);
    graphComponent.setConnectable(false); // Disable edge creation by dragging from vertex
    graphComponent.setDragEnabled(false); // Disable dragging vertices
    frame.setSize(900, 900);
    frame.setVisible(true);

    try {
      Thread.sleep(1000000000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

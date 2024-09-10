package de.monticore.workflow.conformance.datastructure.jwf;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import de.monticore.workflow.conformance.datastructure.interf.WfEdge;
import de.monticore.workflow.conformance.datastructure.interf.WfGraph;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.se_rwth.commons.logging.Log;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JwfGraph implements WfGraph {
  private final DefaultDirectedGraph<WfNode, JwfEdge> graph ;
  private Set<WfNode> nodes;

    public JwfGraph() {
        this.graph =    new DefaultDirectedGraph<>(JwfEdge.class);
        nodes = new HashSet<>();
    }


  @Override
  public void addNode(WfNode node) {
      checkNodePreconditions(node);
    graph.addVertex(node);

  }

  @Override
  public void addEdge(WfNode form, WfNode to) {
      checkEdgePreconditions(form,to);
    graph.addEdge(form, to);
  }

  private void  checkNodePreconditions(WfNode node){
      Set<String> nodeLabels = nodes.stream().map(WfNode::getLabel).collect(Collectors.toSet());
      if (nodeLabels.contains(node.getLabel())){
        Log.error("Adding Node: Duplicate Label "+ node.getLabel()); //todo: print better error
        assert  false;
      }
    nodes.add(node);
  }

  private void  checkEdgePreconditions(WfNode src, WfNode tgt){
    Set<String> nodeLabels = nodes.stream().map(WfNode::getLabel).collect(Collectors.toSet());
    if (!nodeLabels.contains(src.getLabel())) {
      Log.error("Node  "+src.getLabel()+" not present "); //todo: print better error
        assert  false;
    }

    if (!nodeLabels.contains(tgt.getLabel())) {
      Log.error("Adding Edge: Node  "+tgt.getLabel()+" not present "); //todo: print better error
        assert  false;
    }


    }

    public   void visualize() {
        System.out.println("Start Visualizing");
        int i = 0;
        // Create JGraphXAdapter for visualization
        JGraphXAdapter<WfNode, JwfEdge> jgxAdapter = new JGraphXAdapter<>(graph);

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

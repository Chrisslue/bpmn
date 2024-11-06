package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.workflow.conformance.datastructure.analysis.ConfWfBuilder;
import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;
import de.monticore.workflow.conformance.datastructure.analysis.WfElementVisitor;
import de.se_rwth.commons.logging.Log;
import java.util.HashSet;
import java.util.Set;

public class CTLGenerator {

  public CTLGraph bpmn2ctl(ASTWorkflowCompilationUnit ast) {

    // transform ast into intermediate data structure
    CTLGraph graph = new CTLGraph();
    IdWfNode start = generateNode(ast, "");

    // case we are at the root node

    TokenController root = new TokenController(Set.of(start), Set.of(start));
    graph.addNode(CTLNode.mkNode(root.getLabels()));

    // create root node of CTL
    addSuccessorsRecursive(root, graph);

    return graph;
  }

  public void addSuccessorsRecursive(TokenController vertex, CTLGraph graph) {

    for (IdWfNode node : vertex.getActiveNodes()) {

      for (IdWfNode suc : node.getSuccessors()) {

        switch (suc.getNodeType()) {
          case TASK:
          case EVENT:
            TokenController sucNode = doTransition(vertex, Set.of(suc), graph);
            addSuccessorsRecursive(sucNode, graph);
            break;
          case XOR_SPLIT:
            for (IdWfNode xorSuc : suc.getSuccessors()) {
              TokenController xorSucVertex = doTransition(vertex, Set.of(xorSuc), graph);
              addSuccessorsRecursive(xorSucVertex, graph);
            }
            break;
          case XOR_MERGE:
          case AND_SPLIT:
          case AND_MERGE:
            TokenController xorSucVertex = doTransition(vertex, suc.getSuccessors(), graph);
            addSuccessorsRecursive(xorSucVertex, graph);
          case OR_SPLIT:
            break;

          default:
            Log.error("Implementation is coming soon");
            break;
        }
      }
    }
  }

  public TokenController doTransition(
      TokenController previous, Set<IdWfNode> activity, CTLGraph graph) {
    Set<IdWfNode> predElements = new HashSet<>(previous.getLabels());
    predElements.addAll(activity);
    TokenController sucNode = new TokenController(predElements, activity);

    var x = CTLNode.mkNode(sucNode.getLabels());
    graph.addNode(x);

    var x1 = CTLNode.mkNode(previous.getLabels());
    var x2 = CTLNode.mkNode(sucNode.getLabels());

    graph.addEdge(x1, x2);

    return sucNode;
  }

  public IdWfNode generateNode(ASTWorkflowCompilationUnit ast, String prefix) {

    ConfWfBuilder builder = new ConfWfBuilder(prefix);

    // traverse the Workflow ast a collect elements
    WfElementVisitor collector = new WfElementVisitor(builder);
    WorkflowTraverser traverser = WorkflowMill.traverser();
    traverser.add4Workflow(collector);
    ast.accept(traverser);

    return builder.build();
  }
}

package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.monticore.bpmn.workflow._ast.ASTGateway;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public class ProcessHasNoSyncDeadlock extends CommonAntiPatternCoCo {

  @Override
  protected void check(
      Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraph, ASTFlowElementContainer process) {
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(new ProcessHasNoSyncDeadlockVisitor());
    process.accept(traverser);
  }

  class ProcessHasNoSyncDeadlockVisitor implements WorkflowVisitor2 {
    @Override
    public void visit(final ASTGateway mergeGateway) {
      if (mergeGateway.isConverging() && mergeGateway.getType().isParallel()) {
        Sets.combinations(mergeGateway.getPredecessors(), 2).stream()
            .map(Lists::newArrayList)
            .forEach(
                pair -> {
                  // Step 1: Find split gateway(s) (LCA)
                  Optional<ASTFlowNode> lca =
                      Optional.ofNullable(lcaFinder.getLCA(pair.get(0), pair.get(1)));
                  if (!lca.isPresent()) { // parallel gateway used to merge independent branches
                    reportDeadlock(mergeGateway);
                  } else {
                    Stream.of(lca.get()) // TODO check why lcaFinder#getLCASet returns to many nodes
                        .flatMap(WorkflowFilters::isGateway)
                        .forEach(
                            splitGateway -> {
                              if (!splitGateway.getType().isParallel()
                                  && !splitGateway.getType().isParallelEventBased()) {
                                // Step 2: Report deadlock (deterministic xor non-deterministic)
                                reportDeadlock(splitGateway, mergeGateway);
                              } else {
                                // Step 3: Find path entries an exits
                                List<GraphPath<ASTFlowNode, EndpointPair<ASTFlowNode>>> paths =
                                    pathFinder.getAllPaths(splitGateway, mergeGateway, true, null);
                                paths.forEach(
                                    path -> {
                                      getPathEntries(path)
                                          .forEach(
                                              entry -> {
                                                reportPathEntry(splitGateway, mergeGateway, entry);
                                              });
                                    });
                                paths.forEach(
                                    path -> {
                                      getPathExits(path)
                                          .forEach(
                                              exit -> {
                                                reportPathExit(splitGateway, mergeGateway, exit);
                                              });
                                    });
                              }
                            });
                  }
                });
      }
    }

    private void reportDeadlock(final ASTGateway mergeGateway) {
      Log.warn(
          Messages.get("0xWFM7006", mergeGateway.getName()),
          mergeGateway.get_SourcePositionStart(),
          mergeGateway.get_SourcePositionEnd());
    }

    private void reportDeadlock(final ASTGateway splitGateway, final ASTGateway mergeGateway) {
      Log.warn(
          Messages.get("0xWFM7001", splitGateway.getName(), mergeGateway.getName()),
          mergeGateway.get_SourcePositionStart(),
          mergeGateway.get_SourcePositionEnd());
    }

    private void reportPathEntry(
        final ASTGateway splitGateway, final ASTGateway mergeGateway, final ASTFlowNode entry) {
      Log.warn(
          Messages.get("0xWFM7004", entry.getName(), splitGateway.getName()),
          entry.get_SourcePositionStart(),
          entry.get_SourcePositionEnd());
    }

    private void reportPathExit(
        final ASTGateway splitGateway, final ASTGateway mergeGateway, final ASTFlowNode exit) {
      Log.warn(
          Messages.get("0xWFM7003", exit.getName(), mergeGateway.getName()),
          exit.get_SourcePositionStart(),
          exit.get_SourcePositionEnd());
    }
  }
}

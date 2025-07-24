/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.ASTWFProcess;
import de.monticore.bpmn.workflow._ast.ASTFlowElement;
import de.monticore.bpmn.workflow._ast.ASTWFGateway;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import de.se_rwth.commons.logging.Log;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;

public class ProcessHasNoLackOfSync extends CommonAntiPatternCoCo implements WorkflowVisitor2 {
  
  @Override
  protected void check(Graph<ASTFlowElement, EndpointPair<ASTFlowElement>> processGraph,
      ASTWFProcess process) {
    WorkflowTraverser traverser = WorkflowMill.inheritanceTraverser();
    traverser.add4Workflow(new ProcessHasNoLackOfSyncVisitor());
    process.accept(traverser);
  }
  
  class ProcessHasNoLackOfSyncVisitor implements WorkflowVisitor2 {
    
    @Override
    public void visit(final ASTWFGateway mergeGateway) {
      if (mergeGateway.isConverging() && mergeGateway.getType().isExclusive()) {
        Sets.combinations(mergeGateway.getPredecessors(), 2).stream().map(Lists::newArrayList)
            .forEach(pair -> {
              // Step 1: Find split gateway(s) (LCA)
              Stream.of(lcaFinder.getLCA(pair.get(0), pair.get(1))) // TODO check why lcaFinder#getLCASet returns to many nodes
                  .filter(Objects::nonNull).flatMap(WorkflowFilters::isGateway).forEach(
                      splitGateway -> {
                        if (!splitGateway.getType().isExclusive() && !splitGateway.getType()
                            .isExclusiveEventBased()) {
                          // Step 2: Report lack of sync (deterministic xor non-deterministic)
                          reportLackOfSync(splitGateway, mergeGateway);
                        }
                        else {
                          // Step 3: Find path entries
                          List<GraphPath<ASTFlowElement, EndpointPair<ASTFlowElement>>> paths =
                              pathFinder.getAllPaths(splitGateway, mergeGateway, true, null);
                          paths.forEach(path -> {
                            getPathEntries(path).forEach(entry -> {
                              reportPathEntry(splitGateway, mergeGateway, entry);
                            });
                          });
                          // Path exits do not cause any problems for exclusive gateways.
                        }
                      });
            });
      }
    }
    
    private void reportLackOfSync(final ASTWFGateway splitGateway,
        final ASTWFGateway mergeGateway) {
      Log.warn(Messages.get("0xWFM7002", splitGateway.getName(), mergeGateway.getName()),
          splitGateway.get_SourcePositionStart(), splitGateway.get_SourcePositionEnd());
    }
    
    private void reportPathEntry(final ASTWFGateway splitGateway, final ASTWFGateway mergeGateway,
        final ASTFlowElement entry) {
      Log.warn(Messages.get("0xWFM7005", entry.getName(), splitGateway.getName()), entry
          .get_SourcePositionStart(), entry.get_SourcePositionEnd());
    }
    
  }
  
}

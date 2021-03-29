package de.monticore.bpmn.cocos.analysis;

import com.google.common.collect.Sets;
import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.analysis.graph.ReachabilityInspector;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.se_rwth.commons.logging.Log;
import org.jgrapht.Graph;

import java.util.Set;

public class ProcessHasNoDeadNodes extends ProcessGraphCoCo {

    @Override
    protected void check(final Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraph, final ASTFlowElementContainer process) {
        final Set<ASTFlowNode> startNodes = Sets.newHashSet(WorkflowCollectors.toStartNodesLocal(process));

        Sets.difference(processGraph.vertexSet(), new ReachabilityInspector<>(processGraph).reachableFrom(startNodes))
                .forEach(node ->
                        Log.error(Messages.get("0xWFM7009", node.getName()), node.get_SourcePositionStart(), node.get_SourcePositionEnd())
                );
    }

}

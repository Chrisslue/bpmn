package de.monticore.bpmn.cocos.analysis;

import com.google.common.graph.EndpointPair;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.utils.WorkflowFilters;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import de.se_rwth.commons.logging.Log;
import org.jgrapht.Graph;
import org.jgrapht.alg.cycle.CycleDetector;

public class ProcessHasNoInfiniteLoop extends ProcessGraphCoCo {

    @Override
    protected void check(final Graph<ASTFlowNode, EndpointPair<ASTFlowNode>> processGraph, final ASTFlowElementContainer process) {
        new CycleDetector<>(processGraph).findCycles()
                .stream()
                .flatMap(WorkflowFilters::isGateway)
                .filter(gateway -> !gateway.getType().isExclusive() && !gateway.getType().isExclusiveEventBased())
                .forEach(gateway -> {
                    if (gateway.isDiverging()) {  // loop exit
                        Log.warn(Messages.get("0xWFM7007", gateway.getName()),
                                gateway.get_SourcePositionStart(), gateway.get_SourcePositionEnd());
                    }
                    if (gateway.isConverging()
                            && (gateway.getType().isParallel() || gateway.getType().isParallelEventBased())) { // loop entry
                        Log.warn(Messages.get("0xWFM7008", gateway.getName()),
                                gateway.get_SourcePositionStart(), gateway.get_SourcePositionEnd());
                    }
                });
    }

}

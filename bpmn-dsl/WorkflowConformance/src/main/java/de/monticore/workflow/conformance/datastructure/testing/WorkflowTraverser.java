package de.monticore.workflow.conformance.datastructure.testing;

import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.monticore.workflow.conformance.datastructure.ctl.CTLNode;
import de.monticore.workflow.conformance.datastructure.ctl.WfPredicate;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class WorkflowTraverser {

    public void traverse(WfPredicate visitor, WfNode start){
        switch (start.getNodeType()) {
            case EVENT:
            case TASK:
                visitor.apply(start, List.of());
                assert start.getSuccessors().size() == 1; // I don't know how to handle this otherwise
                // NO BREAK!
            case AND_SPLIT:
            case AND_MERGE:
            case XOR_MERGE:
                start.getSuccessors().forEach(succ -> this.traverse(visitor, succ));
                break;

            case XOR_SPLIT:
                for(WfNode succ: start.getSuccessors()){
                    this.traverse(((node, branchId) -> {
                        var newBranchId = new ArrayList<>(branchId);
                        newBranchId.add(succ);
                        visitor.apply(node, newBranchId);
                    }), succ);
                }
                break;

            case OR_SPLIT:
            case OR_MERGE:
                assert false; // TODO
        }
    }
}

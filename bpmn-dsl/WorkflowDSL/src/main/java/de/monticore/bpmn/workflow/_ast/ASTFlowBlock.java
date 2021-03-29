package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Multimaps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ASTFlowBlock extends ASTFlowBlockTOP {

    protected ASTFlowBlock() {
        super();
    }

    protected ASTFlowBlock(final List<ASTFlowBranch> branches) {
        super(branches);
    }

    /**
     * Calculates all entry flow nodes corresponding to this block
     *
     * @return input:
     * ... -> split xor {
     * [cond1]: {
     * [cond2]: TaskA -> ...,
     * [cond3]: TaskB -> ...,
     * default: TaskC -> ...
     * },
     * [cond4]: TaskA
     * } merge xor -> TaskE;
     * <p>
     * output: [TaskA:[cond1, cond2], TaskB:[cond1, cond3], TaskC:[cond1, default], TaskA:[cond4]]
     */
    ListMultimap<ASTFlowNode, List<ASTFlowCondition>> asTarget() {
        return getBranchList()
                .stream()
                .map(ASTFlowBranch::asTarget)
                .map(Multimap::entries)
                .flatMap(Collection::stream)
                .collect(Multimaps.toMultimap(Map.Entry::getKey, Map.Entry::getValue, MultimapBuilder.hashKeys().arrayListValues()::build));
    }

    List<ASTFlowNode> asSource() {
        return getBranchList()
                .stream()
                .map(ASTFlowBranch::asSource)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}

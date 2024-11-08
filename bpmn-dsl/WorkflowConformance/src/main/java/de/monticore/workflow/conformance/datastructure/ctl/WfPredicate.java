package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IDWfNode;
import de.monticore.workflow.conformance.datastructure.interf.WfNode;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class WfPredicate implements BiFunction<WfNode, List<WfNode>,AbortRule> {


    @Override
    public AbortRule apply(WfNode wfNode, List<WfNode> wfNodes) {
        return null;
    }
}

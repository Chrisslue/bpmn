package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CTLNode {
    private final Set<IdWfNode> labels;

    private static final Set<CTLNode> ctlNodes  = new HashSet<>();

    private CTLNode(Set<IdWfNode> labels) {
        this.labels = Collections.unmodifiableSet(labels);
        ctlNodes.add(this);
    }

    public static CTLNode mkNode(Set<IdWfNode> labels) {
        return getNode(labels).orElseGet(() -> new CTLNode(labels));
    }

    public static Optional<CTLNode> getNode(Set<IdWfNode> labels) {
    var res = ctlNodes.stream().filter(node-> node.labels.containsAll(labels) && labels.containsAll(node.labels)).findAny();

    return res;
    }

    @Override
    public String toString() {
        return "[" + labels.toString() + "]";
    }
}

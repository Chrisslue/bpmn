package de.monticore.workflow.conformance.datastructure.ctl;

import de.monticore.workflow.conformance.datastructure.analysis.IdWfNode;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CTLNode {
    private final Set<IdWfNode> labels;

    private static Set<CTLNode> ctlNodes  = new HashSet<>();

    private CTLNode(Set<IdWfNode> labels) {
        this.labels = Collections.unmodifiableSet(labels);
    }

    public static CTLNode mkNode(Set<IdWfNode> labels) {

        return getNode(labels).orElse(new CTLNode(labels));

    }




    public static Optional<CTLNode> getNode(Set<IdWfNode> labels) {
    return ctlNodes.stream().filter(node-> node.labels.containsAll(labels) && labels.containsAll(node.labels)).findAny();
    }

    public Set<IdWfNode> getLabels() {
        return labels;
    }

    @Override
    public String toString() {
        return "[" + labels.toString() + "]";
    }
}

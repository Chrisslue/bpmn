package de.monticore.bpmn.analysis.graph;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Sets;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphLevelIterator<V> extends AbstractIterator<Set<V>> {

    private final Graph<V, ?> graph;

    private final V root;

    private Set<V> level = null;

    private int depth = -1;

    private final Set<V> seen = Sets.newHashSet();


    public GraphLevelIterator(final Graph<V, ?> graph, final V root) {
        this.graph = graph;
        this.root = root;
    }

    @Override
    protected Set<V> computeNext() {
        depth++;

        level = level == null ? Sets.newHashSet(root) : computeNext(level);
        seen.addAll(level);

        return !level.isEmpty() ? level : endOfData();
    }

    private Set<V> computeNext(final Set<V> level) {
        return level.stream().map(v -> Graphs.successorListOf(graph, v))
                .flatMap(Collection::stream)
                .filter(v -> !seen.contains(v))
                .collect(Collectors.toSet());
    }

    public int getDepth() {
        return depth;
    }

}

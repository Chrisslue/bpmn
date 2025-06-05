 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.analysis.graph;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.FloydWarshallShortestPaths;

/**
 * Tests if a node can be reached from at least one node in a set of start nodes.
 *
 * @param <V> the type of node
 */
public class ReachabilityInspector<V> {

  private final Graph<V, ?> graph;

  public ReachabilityInspector(final Graph<V, ?> graph) {
    this.graph = graph;
  }

  public Set<V> reachableFrom(final Set<V> sources) {
    return sources.stream()
        .map(source -> new FloydWarshallShortestPaths<>(graph).getPaths(source))
        .map(
            shortestPaths ->
                graph.vertexSet().stream()
                    .map(shortestPaths::getPath)
                    .filter(Objects::nonNull)
                    .map(GraphPath::getEndVertex))
        .flatMap(Function.identity())
        .collect(Collectors.toSet());
  }
}

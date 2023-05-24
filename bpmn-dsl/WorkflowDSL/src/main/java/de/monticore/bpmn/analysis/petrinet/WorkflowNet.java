package de.monticore.bpmn.analysis.petrinet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.ASTFlowElementContainer;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import petrinet._ast.ASTPetriNode;
import petrinet._ast.ASTPetrinet;
import petrinet._ast.ASTPlace;
import petrinet._ast.ASTTransition;

/**
 * A Workflow net (WF-net) is Petri net with a unique source place (input place) and a unique sink
 * place (output place).
 *
 * <p>A "short-circuited" WF-net has an extra transition connecting the sink place to the source
 * place of the WF-net. The short-circuited WF-net is useful for analysis, e.g., when checking
 * liveness.
 *
 * <p>http://mlwiki.org/index.php/Workflow_Nets
 */
public class WorkflowNet {

  // underlying petrinet
  private final ASTPetrinet petrinet;

  // unique source
  private final ASTPlace source;

  // unique sink
  private final ASTPlace sink;

  // warnings generated during translation from the BPMN process or sub-process into the WF-net
  private final List<WorkflowNetConverter.Warning> warnings;

  // reverse mapping from Petri net nodes to BPMN flow nodes
  private final Map<ASTPetriNode, Set<ASTFlowNode>> mapping;

  // transition connecting sink to source
  private ASTTransition shortCircuit;

  public WorkflowNet(
      final ASTPetrinet petrinet,
      final ASTPlace source,
      final ASTPlace sink,
      final List<WorkflowNetConverter.Warning> warnings,
      final Map<ASTPetriNode, Set<ASTFlowNode>> mapping) {
    this.petrinet = petrinet;
    this.source = source;
    this.sink = sink;
    this.warnings = warnings;
    this.mapping = mapping;

    if (petrinet.getEnclosingScope() == null) {
      PetriNetUtils.buildSymTab(petrinet);
    }
  }

  /**
   * Creates the WF-net for a process or sub-process.
   *
   * <p>The WF-net only includes the given process level. Contained sub-processes must be handled
   * separately.
   *
   * @param container the process or sub-process
   * @return the WF-net for the process or sub-process
   */
  public static WorkflowNet from(final ASTFlowElementContainer container) {
    return new WorkflowNetConverter(container).convert();
  }

  /**
   * Creates the initial marking for a WF-net, i.e., i.e. a marking that has only a token at the
   * source place.
   *
   * @param wfNet the WF-net
   * @return the initial marking
   */
  public static Map<ASTPlace, Long> initialMarking(final WorkflowNet wfNet) {
    final Map<ASTPlace, Long> marking = Maps.newHashMap();
    marking.put(wfNet.getSource(), 1L);

    return marking;
  }

  /**
   * Returns the underlying Petri net.
   *
   * @return the Petri net.
   */
  public ASTPetrinet getPetriNet() {
    return petrinet;
  }

  /**
   * Returns the places.
   *
   * @return the places
   */
  public Set<ASTPlace> getPlaces() {
    return Sets.newHashSet(petrinet.getPlaceList());
  }

  /**
   * Returns the places, not including the the source and the sink place.
   *
   * @return the places
   */
  public Set<ASTPlace> getPlacesNotSourceOrSink() {
    return Sets.difference(getPlaces(), Sets.newHashSet(source, sink));
  }

  /**
   * Returns the transitions.
   *
   * @return the transitions
   */
  public Set<ASTTransition> getTransitions() {
    return Sets.newHashSet(petrinet.getTransitionList());
  }

  /**
   * Returns the source.
   *
   * @return the source
   */
  public ASTPlace getSource() {
    return source;
  }

  /**
   * Returns the sink.
   *
   * @return the sink
   */
  public ASTPlace getSink() {
    return sink;
  }

  /**
   * Returns the short-circuit transition that connects the sink to source place, if it was
   * previously added.
   *
   * @see WorkflowNet#connectSinkToSource()
   * @return optional containing the short-circuit transition or empty optional if it has not been
   *     added
   */
  public Optional<ASTTransition> getShortCircuit() {
    return Optional.ofNullable(shortCircuit);
  }

  /**
   * Returns the warnings generated during translation from the BPMN process or sub-process into the
   * WF-net.
   *
   * @return the warnings
   */
  public List<WorkflowNetConverter.Warning> getWarnings() {
    return warnings;
  }

  /**
   * Returns the reverse mapping from Petri net nodes to BPMN flow nodes.
   *
   * @return the mapping
   */
  public Map<ASTPetriNode, Set<ASTFlowNode>> getMapping() {
    return mapping;
  }

  /**
   * Adds a transition connecting the sink place to the source place of the WF-net.
   *
   * @return this
   */
  public WorkflowNet connectSinkToSource() {
    shortCircuit = PetriNetUtils.connect(sink, source, "p_sink_source");
    return this;
  }
}

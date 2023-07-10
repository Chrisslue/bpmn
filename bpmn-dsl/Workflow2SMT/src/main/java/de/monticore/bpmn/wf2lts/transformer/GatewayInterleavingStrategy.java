package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;

public interface GatewayInterleavingStrategy {

  default LTS interleave(GatewayScope.GatewayType type, LTS graph) {
    switch (type) {
      case XOR:
        return interleaveExclusive(graph);
      case IOR:
        return interleaveInclusive(graph);
      case PARALLEL:
        return interleaveParallel(graph);
      case EVENT_PARALLEL:
        return interleaveEventBasedParallel(graph);
      case EVENT_XOR:
        return interleaveEventBasedExclusive(graph);
      case COMPLEX:
        return interleaveComplex(graph);
      default:
        throw new IllegalArgumentException("Passed unexpected GatewayType: " + type);
    }
  }

  LTS interleaveExclusive(LTS graph);

  LTS interleaveParallel(LTS graph);

  LTS interleaveInclusive(LTS graph);

  LTS interleaveEventBasedParallel(LTS graph);

  LTS interleaveEventBasedExclusive(LTS graph);

  LTS interleaveComplex(LTS graph);
}

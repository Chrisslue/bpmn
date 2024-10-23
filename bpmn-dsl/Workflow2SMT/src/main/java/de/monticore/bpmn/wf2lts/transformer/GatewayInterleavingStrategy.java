package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;

public interface GatewayInterleavingStrategy {

  default LTSWithFinalStates interleave(
      GatewayScope.GatewayType type, LTSWithFinalStates ltsWithFinalStates) {
    switch (type) {
      case XOR:
        return interleaveExclusive(ltsWithFinalStates);
      case IOR:
        return interleaveInclusive(ltsWithFinalStates);
      case PARALLEL:
        return interleaveParallel(ltsWithFinalStates);
      case EVENT_PARALLEL:
        return interleaveEventBasedParallel(ltsWithFinalStates);
      case EVENT_XOR:
        return interleaveEventBasedExclusive(ltsWithFinalStates);
      case COMPLEX:
        return interleaveComplex(ltsWithFinalStates);
      default:
        throw new IllegalArgumentException("Passed unexpected GatewayType: " + type);
    }
  }

  LTSWithFinalStates interleaveExclusive(LTSWithFinalStates ltsWithFinalStates);

  LTSWithFinalStates interleaveParallel(LTSWithFinalStates ltsWithFinalStates);

  LTSWithFinalStates interleaveInclusive(LTSWithFinalStates ltsWithFinalStates);

  LTSWithFinalStates interleaveEventBasedParallel(LTSWithFinalStates ltsWithFinalStates);

  LTSWithFinalStates interleaveEventBasedExclusive(LTSWithFinalStates ltsWithFinalStates);

  LTSWithFinalStates interleaveComplex(LTSWithFinalStates ltsWithFinalStates);
}

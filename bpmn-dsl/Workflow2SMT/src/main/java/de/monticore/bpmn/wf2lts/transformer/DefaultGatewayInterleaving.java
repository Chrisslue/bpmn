package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;

public class DefaultGatewayInterleaving implements GatewayInterleavingStrategy {

  @Override
  public LTSWithFinalStates interleaveExclusive(LTSWithFinalStates lts) {
    return lts; // Noting to do
  }

  @Override
  public LTSWithFinalStates interleaveParallel(LTSWithFinalStates lts) {
    return DefaultParallelInterleaving.interleave(lts);
  }

  @Override
  public LTSWithFinalStates interleaveInclusive(LTSWithFinalStates lts) {
    return DefaultSequentialInterleaving.interleave(lts);
  }

  @Override
  public LTSWithFinalStates interleaveEventBasedParallel(LTSWithFinalStates lts) {
    return DefaultParallelInterleaving.interleave(lts);
  }

  @Override
  public LTSWithFinalStates interleaveEventBasedExclusive(LTSWithFinalStates lts) {
    return lts; // Nothing to do.
  }

  @Override
  public LTSWithFinalStates interleaveComplex(LTSWithFinalStates lts) {
    throw new UnsupportedOperationException("interleaveComplex is not yet implemented");
  }
}

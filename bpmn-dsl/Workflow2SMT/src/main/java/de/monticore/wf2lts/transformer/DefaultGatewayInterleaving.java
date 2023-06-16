package de.monticore.wf2lts.transformer;

import de.monticore.wf2lts.datastructure.LTS;

public class DefaultGatewayInterleaving implements GatewayInterleavingStrategy {

  @Override
  public LTS interleaveExclusive(LTS lts) {
    return lts; // Noting to do
  }

  @Override
  public LTS interleaveParallel(LTS lts) {
    return DefaultParallelInterleaving.interleave(lts);
  }

  @Override
  public LTS interleaveInclusive(LTS lts) {
    return DefaultSequentialInterleaving.interleave(lts);
  }

  @Override
  public LTS interleaveEventBasedParallel(LTS lts) {
    return DefaultParallelInterleaving.interleave(lts);
  }

  @Override
  public LTS interleaveEventBasedExclusive(LTS lts) {
    return lts; // Nothing to do.
  }

  @Override
  public LTS interleaveComplex(LTS lts) {
    throw new UnsupportedOperationException("interleaveComplex is not yet implemented");
  }

}

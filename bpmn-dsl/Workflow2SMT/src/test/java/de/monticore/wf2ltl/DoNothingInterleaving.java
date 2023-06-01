package de.monticore.wf2ltl;

import de.monticore.wf2ltl.datastructure.LTS;
import de.monticore.wf2ltl.scopes.GatewayScope.GatewayType;
import de.monticore.wf2ltl.transformer.GatewayInterleavingStrategy;

public class DoNothingInterleaving implements GatewayInterleavingStrategy {

  @Override
  public LTS interleave(GatewayType type, LTS graph) {
    return graph;
  }

  @Override
  public LTS interleaveExclusive(LTS graph) {
    return graph;
  }

  @Override
  public LTS interleaveParallel(LTS graph) {
    return graph;
  }

  @Override
  public LTS interleaveInclusive(LTS graph) {
    return graph;
  }

  @Override
  public LTS interleaveEventBasedParallel(LTS graph) {
    return graph;
  }

  @Override
  public LTS interleaveEventBasedExclusive(LTS graph) {
    return graph;
  }

  @Override
  public LTS interleaveComplex(LTS graph) {
    return graph;
  }
}

package de.monticore.bpmn.wf2lts;

import de.monticore.bpmn.wf2lts.datastructure.LTSWithFinalStates;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope.GatewayType;
import de.monticore.bpmn.wf2lts.transformer.GatewayInterleavingStrategy;

public class DoNothingInterleaving implements GatewayInterleavingStrategy {

  @Override
  public LTSWithFinalStates interleave(GatewayType type, LTSWithFinalStates lts) {
    return lts;
  }

  @Override
  public LTSWithFinalStates interleaveExclusive(LTSWithFinalStates lts) {
    return lts;
  }

  @Override
  public LTSWithFinalStates interleaveParallel(LTSWithFinalStates lts) {
    return lts;
  }

  @Override
  public LTSWithFinalStates interleaveInclusive(LTSWithFinalStates lts) {
    return lts;
  }

  @Override
  public LTSWithFinalStates interleaveEventBasedParallel(LTSWithFinalStates lts) {
    return lts;
  }

  @Override
  public LTSWithFinalStates interleaveEventBasedExclusive(LTSWithFinalStates lts) {
    return lts;
  }

  @Override
  public LTSWithFinalStates interleaveComplex(LTSWithFinalStates lts) {
    return lts;
  }
}

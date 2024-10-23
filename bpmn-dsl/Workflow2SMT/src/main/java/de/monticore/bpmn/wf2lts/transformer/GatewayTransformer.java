package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;

public interface GatewayTransformer {

  GatewayInterleavingStrategy getGatewayInterleavingStrategy();

  LTS transform(
      GatewayScope gatewayScope, LTS externalLTS, Graph2LTSTransformer graph2LTSTransformer);
}

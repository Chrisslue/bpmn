package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.scopes.GatewayScope;
import de.monticore.bpmn.workflow._ast.IFlowNode;

public interface GatewayTransformer {

  GatewayInterleavingStrategy getGatewayInterleavingStrategy();

  void transform(
      GatewayScope gatewayScope,
      LTS externalGraph,
      NamingStrategy<IFlowNode> namingStrategy,
      Graph2LTSTransformer graphTransformer);
}

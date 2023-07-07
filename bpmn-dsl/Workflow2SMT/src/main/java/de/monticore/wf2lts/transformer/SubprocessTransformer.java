package de.monticore.wf2lts.transformer;

import de.monticore.bpmn.workflow._ast.IFlowNode;
import de.monticore.wf2lts.NamingStrategy;
import de.monticore.wf2lts.datastructure.LTS;
import de.monticore.wf2lts.scopes.SubProcessScope;

public interface SubprocessTransformer {

  void transform(
      SubProcessScope subProcessScope,
      LTS externalGraph,
      NamingStrategy<IFlowNode> namingStrategy,
      Graph2LTSTransformer graphTransformer);
}

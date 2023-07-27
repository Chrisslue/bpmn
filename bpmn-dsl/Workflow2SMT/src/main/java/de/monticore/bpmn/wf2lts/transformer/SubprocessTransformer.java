package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.NamingStrategy;
import de.monticore.bpmn.wf2lts.datastructure.LTS;
import de.monticore.bpmn.wf2lts.scopes.SubProcessScope;
import de.monticore.bpmn.workflow._ast.ASTFlowNode;

public interface SubprocessTransformer {

  void transform(
      SubProcessScope subProcessScope,
      LTS externalGraph,
      NamingStrategy<ASTFlowNode> namingStrategy,
      Graph2LTSTransformer graphTransformer);
}

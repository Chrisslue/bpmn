package de.monticore.bpmn.wf2lts.transformer;

import de.monticore.bpmn.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.bpmn.wf2lts.datastructure.LTS;

public interface Graph2LTSTransformer {

  LTS transform(IntermediateGraphWithScopes graph);

  LTS transformAndReduce(IntermediateGraphWithScopes graph);
}

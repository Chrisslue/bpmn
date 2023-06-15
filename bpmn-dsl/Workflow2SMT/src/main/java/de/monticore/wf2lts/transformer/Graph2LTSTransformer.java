package de.monticore.wf2lts.transformer;

import de.monticore.wf2lts.datastructure.IntermediateGraphWithScopes;
import de.monticore.wf2lts.datastructure.LTS;

public interface Graph2LTSTransformer {

  LTS transform(IntermediateGraphWithScopes graph);

}

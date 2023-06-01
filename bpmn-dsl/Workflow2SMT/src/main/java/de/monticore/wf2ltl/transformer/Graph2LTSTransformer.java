package de.monticore.wf2ltl.transformer;

import de.monticore.wf2ltl.datastructure.IntermediateGraphWithScopes;
import de.monticore.wf2ltl.datastructure.LTS;

public interface Graph2LTSTransformer {

  LTS transform(IntermediateGraphWithScopes graph);

}

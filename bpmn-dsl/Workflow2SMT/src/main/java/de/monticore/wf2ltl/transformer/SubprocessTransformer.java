package de.monticore.wf2ltl.transformer;

import de.monticore.wf2ltl.NamingStrategy;
import de.monticore.wf2ltl.datastructure.LTS;
import de.monticore.wf2ltl.scopes.SubProcessScope;

public interface SubprocessTransformer {

  void transform(SubProcessScope subProcessScope, LTS externalGraph, NamingStrategy namingStrategy,
      GatewayTransformer gatewayTransformer);

}
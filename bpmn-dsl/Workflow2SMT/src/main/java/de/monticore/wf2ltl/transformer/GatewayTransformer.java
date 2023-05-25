package de.monticore.wf2ltl.transformer;

import de.monticore.wf2ltl.NamingStrategy;
import de.monticore.wf2ltl.datastructure.LTS;
import de.monticore.wf2ltl.scopes.GatewayScope;

public interface GatewayTransformer {

  GatewayInterleavingStrategy getGatewayInterleavingStrategy();

  void transform(GatewayScope gatewayScope, LTS externalGraph, NamingStrategy namingStrategy,
      SubprocessTransformer subprocessTransformer);

}

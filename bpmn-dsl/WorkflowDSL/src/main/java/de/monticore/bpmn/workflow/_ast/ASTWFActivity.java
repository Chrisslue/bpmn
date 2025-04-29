 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.workflow._ast;

import java.util.*;

public interface ASTWFActivity extends ASTWFActivityTOP {

  Collection<? extends ASTWFEvent> getBoundaryEvents();

  default boolean isForCompensation() {
    return getSymbol().isCompensating();
  }
}

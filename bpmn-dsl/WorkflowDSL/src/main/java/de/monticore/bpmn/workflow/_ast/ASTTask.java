package de.monticore.bpmn.workflow._ast;

import java.util.Collection;

public class ASTTask extends ASTTaskTOP {

  protected ASTTask() {
    super();
  }

  @Override
  public Collection<? extends ASTEvent> getBoundaryEvents() {
    return getBoundaryEventList();
  }
}

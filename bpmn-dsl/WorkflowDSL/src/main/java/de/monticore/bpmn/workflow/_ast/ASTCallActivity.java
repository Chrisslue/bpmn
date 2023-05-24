package de.monticore.bpmn.workflow._ast;

import java.util.Collection;

public class ASTCallActivity extends ASTCallActivityTOP {

  @Override
  public Collection<? extends ASTEvent> getBoundaryEvents() {
    return getBoundaryEventList();
  }
}

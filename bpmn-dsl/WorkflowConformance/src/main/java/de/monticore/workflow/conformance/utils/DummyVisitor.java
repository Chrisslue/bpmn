package de.monticore.workflow.conformance.utils;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor2;
import java.util.*;

public class DummyVisitor implements WorkflowVisitor2 {

  private final List<String> taskAndEventNames = new ArrayList<>();

  @Override
  public void visit(ASTNamedEvent node) {
    taskAndEventNames.add(node.getName());
  }

  @Override
  public void visit(ASTTask node) {
    taskAndEventNames.add(node.getName());
  }

  public List<String> getTaskAndEventNames() {
    return taskAndEventNames;
  }
}

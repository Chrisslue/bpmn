package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.io.IOSpecification;
import java.util.Optional;

public class TaskSymbol extends TaskSymbolTOP {

  private IOSpecification ioSpecification;

  public TaskSymbol(final String name) {
    super(name);
  }

  public TaskSymbol(final String name, final IOSpecification ioSpecification) {
    super(name);
    this.ioSpecification = ioSpecification;
  }

  public Optional<IOSpecification> getIoSpecification() {
    return Optional.ofNullable(ioSpecification);
  }

  public void setIoSpecification(final IOSpecification ioSpecification) {
    this.ioSpecification = ioSpecification;
  }
}

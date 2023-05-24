package de.monticore.bpmn.workflow._ast;

public class ASTInlineGateway extends ASTInlineGatewayTOP {

  private String name;

  public boolean isDiverging() {
    return sizeOutgoings() > 1;
  }

  public boolean isConverging() {
    return sizeIncomings() > 1;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}

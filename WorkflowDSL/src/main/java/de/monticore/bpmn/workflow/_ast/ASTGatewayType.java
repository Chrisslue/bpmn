/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._ast;

public class ASTGatewayType extends ASTGatewayTypeTOP {
  
  public boolean isEventBased() { return isExclusiveEventBased() || isParallelEventBased(); }
  
}

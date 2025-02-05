package de.monticore.bpmn.workflow._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class WFSubProcessSymbolDeSer extends WFSubProcessSymbolDeSerTOP {
  
  // TODO: implement Methods    
  @Override
  protected  WFActivitySymbol deserializeCompensates (JsonObject symbolJson){
    return null;
  }

  @Override
  protected  void serializeCompensates (WFActivitySymbol compensates, WorkflowSymbols2Json s2j){

  }

}
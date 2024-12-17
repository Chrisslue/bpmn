package de.monticore.bpmn.workflow._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

public class SubProcessSymbolDeSer extends SubProcessSymbolDeSerTOP {
  
  // TODO: implement Methods    
  @Override
  protected  ActivitySymbol deserializeCompensates (JsonObject symbolJson){
    return null;
  }

  @Override
  protected  void serializeCompensates (ActivitySymbol compensates, WorkflowSymbols2Json s2j){

  }

}
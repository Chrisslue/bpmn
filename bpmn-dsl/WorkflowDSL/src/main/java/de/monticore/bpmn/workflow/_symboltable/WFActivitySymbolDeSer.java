 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;
import java.util.*;

public class WFActivitySymbolDeSer extends WFActivitySymbolDeSerTOP {
  // TODO: implement Methods  
  @Override
  protected  Optional<WFActivitySymbol> deserializeCompensates (JsonObject symbolJson){
    
    return null;
  }

  @Override
  protected  void serializeCompensates (Optional<WFActivitySymbol> compensates, WorkflowSymbols2Json s2j){

  }

}
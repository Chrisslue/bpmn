/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._ast.io.IOSpecification;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionDeSer;

import java.util.Optional;

public class AtomicActivitySymbolDeSer extends AtomicActivitySymbolDeSerTOP {

  @Override
  protected void serializeIOSpecification(Optional<IOSpecification> iOSpecification, WorkflowSymbols2Json s2j) {

  }

  @Override
  protected Optional<IOSpecification> deserializeIOSpecification(JsonObject symbolJson) {
    return Optional.empty();
  }
}

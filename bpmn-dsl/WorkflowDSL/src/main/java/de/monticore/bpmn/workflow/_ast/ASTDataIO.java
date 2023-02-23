package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.workflow._symboltable.DataObjectSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Optional;

public class ASTDataIO extends ASTDataIOTOP {

    Optional<ASTDataObject> getDataObject() {
        Optional<DataObjectSymbol> dataSymbol = getEnclosingScope()
                .resolveDataObject(getMCQualifiedName().getQName());
        if (dataSymbol.isPresent()) {
            return Optional.ofNullable(dataSymbol.get().getAstNode());
        } else {
            return Optional.empty();
        }
    }

}

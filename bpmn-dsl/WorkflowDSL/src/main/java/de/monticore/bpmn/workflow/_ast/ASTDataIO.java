package de.monticore.bpmn.workflow._ast;

import de.monticore.bpmn.workflow._symboltable.DataObjectSymbol;
import de.monticore.types.types._ast.ASTType;

import java.util.Optional;

public class ASTDataIO extends ASTDataIOTOP {

    protected ASTDataIO() {
        super();
    }

    protected ASTDataIO (
            ASTQName qName,
            Optional<ASTType> type,
            Optional<String> alias,
            boolean loopIO,
            boolean optional,
            boolean whileExecuting
    ) {
        super(qName, type, alias, loopIO, optional, whileExecuting);
    }

    Optional<ASTDataObject> getDataObject() {
        Optional<DataObjectSymbol> dataSymbol = getEnclosingScope()
                .resolve(getQName().getQualifiedName(), DataObjectSymbol.KIND);
        if (dataSymbol.isPresent()) {
            return dataSymbol.get().getDataObjectNode();
        } else {
            return Optional.empty();
        }
    }

}

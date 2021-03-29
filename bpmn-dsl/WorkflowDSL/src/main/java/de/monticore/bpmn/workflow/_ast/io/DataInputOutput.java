package de.monticore.bpmn.workflow._ast.io;

import de.monticore.bpmn.workflow._symboltable.DataObjectSymbol;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.Optional;

public interface DataInputOutput {

    String getName();

    ASTType getType();

    boolean isCollection();

    Optional<DataObjectSymbol> getReferencedDataObject();

    default CDTypeSymbolReference getItemCDTypeSymbolReference() {
        return getReferencedDataObject().get().getTypeSymbolRef();
    }

}

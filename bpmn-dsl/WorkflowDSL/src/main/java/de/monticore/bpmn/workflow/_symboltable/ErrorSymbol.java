package de.monticore.bpmn.workflow._symboltable;

import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.Optional;

public class ErrorSymbol extends ErrorSymbolTOP implements CDTypeReferencingSymbol {

    private CDTypeSymbolReference typeSymbolReference;

    public ErrorSymbol(String name) {
        super(name);
    }

    @Override
    public CDTypeSymbolReference getTypeSymbolRef() {
        return typeSymbolReference;
    }

    public void setTypeSymbolRef(CDTypeSymbolReference typeSymbolRef) {
        this.typeSymbolReference = typeSymbolRef;
    }

}

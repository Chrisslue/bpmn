package de.monticore.bpmn.workflow._symboltable;

import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.Optional;

public class DataObjectSymbol extends DataObjectSymbolTOP implements CDTypeReferencingSymbol {

    private CDTypeSymbolReference typeSymbolRef;

    public DataObjectSymbol(String name) {
        super(name);
    }

    @Override
    public CDTypeSymbolReference getTypeSymbolRef() {
        return typeSymbolRef;
    }

    public void setTypeSymbolRef(final CDTypeSymbolReference typeSymbolRef) {
        this.typeSymbolRef = typeSymbolRef;
    }

}

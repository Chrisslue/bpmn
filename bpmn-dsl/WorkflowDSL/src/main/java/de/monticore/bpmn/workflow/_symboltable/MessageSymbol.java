package de.monticore.bpmn.workflow._symboltable;

import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.Optional;

public class MessageSymbol extends MessageSymbolTOP implements CDTypeReferencingSymbol {

    private CDTypeSymbolReference typeSymbolReference;

    public MessageSymbol(String name) {
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

package de.monticore.bpmn.workflow._symboltable;

import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;

import java.util.Optional;

public class EscalationSymbol extends EscalationSymbolTOP implements CDTypeReferencingSymbol {

    private CDTypeSymbolReference typeSymbolReference;

    public EscalationSymbol(String name) {
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

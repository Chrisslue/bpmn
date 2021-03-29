package de.monticore.bpmn.workflow._symboltable;

public class WorkflowSymbolTable {

    private final WorkflowScope modelScope;

    public WorkflowSymbolTable(WorkflowScope scope) {
        modelScope = scope;
    }

    public WorkflowScope getModelScope() {
        return modelScope;
    }

}

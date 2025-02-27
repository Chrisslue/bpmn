package de.monticore.bpmn.workflow._ast;

public interface ASTWFDataKind extends ASTWFDataKindTOP{
    default boolean isDataObject() {
        return false;
    }

    default boolean isDataStore() {
        return false;
    }
}

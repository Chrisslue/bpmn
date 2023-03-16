package de.monticore.bpmn.workflow._ast.io;

import de.monticore.types.mcbasictypes._ast.ASTMCType;

public interface DataInputOutput {

    String getName();

    ASTMCType getMCType();

    boolean isCollection();

}

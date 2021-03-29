package de.monticore.bpmn.workflow._ast;

import de.se_rwth.commons.Names;

import java.util.List;

public class ASTPackageStatement extends ASTPackageStatementTOP {

    protected ASTPackageStatement() {
        super();
    }

    protected ASTPackageStatement(final List<String> parts) {
        super(parts);
    }

    public String getQualifiedPackage() {
        return Names.getQualifiedName(this.getPartList());
    }

}

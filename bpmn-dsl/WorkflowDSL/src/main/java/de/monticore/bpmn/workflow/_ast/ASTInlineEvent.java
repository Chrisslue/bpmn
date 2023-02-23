package de.monticore.bpmn.workflow._ast;

import java.util.List;
import java.util.Optional;

public class ASTInlineEvent extends ASTInlineEventTOP {

    private String name;

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}

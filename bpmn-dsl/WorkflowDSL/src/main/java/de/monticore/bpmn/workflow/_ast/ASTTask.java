package de.monticore.bpmn.workflow._ast;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTTask extends ASTTaskTOP {

    protected  ASTTask() {
        super();
    }
    @Override
    public Collection<? extends ASTEvent> getBoundaryEvents() {
        return getBoundaryEventList();
    }

}

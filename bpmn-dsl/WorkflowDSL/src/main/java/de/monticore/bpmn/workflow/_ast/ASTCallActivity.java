package de.monticore.bpmn.workflow._ast;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTCallActivity extends ASTCallActivityTOP {

    @Override
    public Collection<? extends ASTEvent> getBoundaryEvents() {
        return getBoundaryEventList();
    }
}

package de.monticore.bpmn.workflow._ast;

import java.util.Collection;

public interface ASTActivity extends ASTActivityTOP {

    Collection<? extends ASTEvent> getBoundaryEvents();

    default boolean isForCompensation() {
        return getCompensationHandlerOpt().isPresent();
    }

}

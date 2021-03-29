package de.monticore.bpmn.workflow._ast;


import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;

import java.util.function.Predicate;

public interface ASTEvent extends ASTEventTOP {

    default boolean isStart() {
        return getTypeOpt().map(ASTEventType.START::equals).orElse(false);
    }

    default boolean isEnd() {
        return getTypeOpt().map(ASTEventType.END::equals).orElse(false);
    }

    default boolean isIntermediate() {
        return !isStart() && !isEnd();
    }

    default boolean isBoundary() {
        return false;
    }

    default boolean isNonInterrupt() {
        return getBehaviorOpt().map(ASTEventBehavior::isNonInterrupt).orElse(false);
    }

    default boolean isCatch() {
        return getBehaviorOpt().map(ASTEventBehavior::isCatch).orElseGet(() ->
                isStart() || isBoundary() || (isIntermediate() && new IsIntermediateCatchTrigger().test(this)));
    }

    default boolean isThrow() {
        return getBehaviorOpt().map(ASTEventBehavior::isThrow).orElseGet(() ->
                isEnd() || (isIntermediate() && new IsIntermediateThrowTrigger().test(this)));
    }

    String getName();

    class IsIntermediateThrowTrigger implements Predicate<ASTEvent>, WorkflowVisitor {
        boolean isThrow;

        @Override
        public boolean test(final ASTEvent event) {
            if (!event.isPresentTrigger()) {
                return true;
            }
            event.accept(this);
            return isThrow;
        }

        @Override
        public void visit(final ASTEventTriggerEscalate trigger) {
            isThrow = true;
        }
        @Override
        public void visit(final ASTEventTriggerCompensate trigger) {
            isThrow = true;
        }
    }

    class IsIntermediateCatchTrigger implements Predicate<ASTEvent>, WorkflowVisitor {
        boolean isCatch;

        @Override
        public boolean test(final ASTEvent event) {
/*            if (event.isBoundary()) {
                return true;
            }*/
            event.accept(this);
            return isCatch;
        }

        @Override
        public void visit(final ASTEventTriggerTimer trigger) {
            isCatch = true;
        }
        @Override
        public void visit(final ASTEventTriggerConditional trigger) {
            isCatch = true;
        }
        @Override
        public void visit(final ASTEventTriggerMultiple trigger) {
            if (!trigger.isParallelMultiple()) {
                isCatch = true;
            }
        }
    }

}

/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.prettyprint;

import de.monticore.bpmn.workflow._visitor.WorkflowDelegatorVisitor;
import de.monticore.common.prettyprint.CommonPrettyPrinterConcreteVisitor;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressions.visitors.TimeExpressionsPrettyConcretePrinterVisitor;
import de.monticore.literals.prettyprint.LiteralsPrettyPrinterConcreteVisitor;
import de.monticore.numberunit.prettyprint.NumberUnitPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;

/**
 * Pretty printer for BPMN.
 *
 * @see WorkflowPrettyPrinterConcreteVisitor
 */
public class WorkflowPrettyPrinter extends WorkflowDelegatorVisitor {

    protected IndentPrinter out;

    public WorkflowPrettyPrinter(IndentPrinter o) {
        out = o;

        // OCL pretty printer cannot be a delegating one, so we need to add all pretty printers manually
        // OCL visitor does not print anything but needs to be present for traversal (OCLNonNumberPrimary and OCLNumberPrimary)
        setOCLVisitor(new OCLPrettyPrinterConcreteVisitor(o));
        setOCLExpressionsVisitor(new OCLExpressionsPrettyPrinterConcreteVisitor(o));
        setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(o));
        setCommonVisitor(new CommonPrettyPrinterConcreteVisitor(o));
        setNumberUnitVisitor(new NumberUnitPrettyPrinter(o));
        setLiteralsVisitor(new LiteralsPrettyPrinterConcreteVisitor(o));
        setTypesVisitor(new TypesPrettyPrinterConcreteVisitor(o));

        setTimeExpressionsVisitor(new TimeExpressionsPrettyConcretePrinterVisitor(o));
        setWorkflowVisitor(new WorkflowPrettyPrinterConcreteVisitor(o));
    }

}

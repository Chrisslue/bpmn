/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._prettyprint;

import de.monticore.bpmn.workflow._ast.*;
import de.monticore.prettyprint.IndentPrinter;

public class WorkflowPrettyPrinter extends WorkflowPrettyPrinterTOP {

  public WorkflowPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  @Override
  public void handle(ASTEvent node) {
    if(node.isPresentName()){
    getPrinter().print("event " + node.getName() + " ");
    }
    else{
    getPrinter().print("event " + " ");
    }
    getTraverser().traverse(node);
    getPrinter().println(";");
  }

  @Override
  public void handle(ASTTask node) {
    getPrinter().print("task " + node.getName() + " ");
    getTraverser().traverse(node);
    getPrinter().println(";");
  }

  @Override
  public void handle(ASTSubProcess node) {
    getPrinter().println("subprocess " + node.getName() + " {");
    getPrinter().indent();
    getTraverser().traverse(node);
    getPrinter().unindent();
    getPrinter().println("}");
  }

  @Override
  public void handle(ASTGateway node) {
    if(node.isPresentName()){
      getPrinter().print("gateway " + node.getName() + " ");
    }
    else{
       getPrinter().print("gateway " + " ");
    }
    getTraverser().traverse(node);
    getPrinter().println(";");
  }

}

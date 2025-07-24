/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.workflow._prettyprint;

import de.monticore.bpmn.workflow._ast.ASTTaskTypeAttributes;
import de.monticore.prettyprint.IndentPrinter;

public class WorkflowPrettyPrinter extends WorkflowPrettyPrinterTOP {
  
  public WorkflowPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }
  
  @Override
  public void handle(ASTTaskTypeAttributes node) {
    if (node.isPresentScript() && node.isPresentScriptFormat()) {
      printer.println("script(" + node.getScriptFormat() + ") = {" + node.getScript());
      printer.println(node.getScript());
      printer.println("};");
    }
    else if (node.getResourcesList() != null && !node.getResourcesList().isEmpty()) {
      printer.print("resources = ");
      for (int i = 0; i < node.getResourcesList().size() - 1; i++) {
        printer.print(node.getResourcesList().get(i));
        printer.print(", ");
      }
      printer.print(node.getResourcesList().get(node.getResourcesList().size() - 1));
      printer.println(";");
    }
    else {
      if (node.isPresentWebservice()) {
        printer.println("webservice = " + node.getWebservice() + ";");
      }
      else {
        printer.print("webservice = ");
        if (node.getNoWebservice() == 7) {
          printer.println("##webservice;");
        }
        else {
          printer.println("##unspecified;");
        }
      }
      if (node.isPresentOperation()) {
        printer.println("operation = " + node.getOperation() + ";");
      }
      if (node.isPresentMessage()) {
        printer.println("message = " + node.getMessage() + ";");
      }
    }
  }
  
}

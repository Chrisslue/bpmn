/* (c) https://github.com/MontiCore/monticore */
package de.monticore.bpmn.timerconditions._prettyprint;

import de.monticore.bpmn.timerconditions._ast.ASTDate;
import de.monticore.bpmn.timerconditions._ast.ASTEveryTimeCondition;
import de.monticore.bpmn.timerconditions._ast.ASTOnDateCondition;
import de.monticore.bpmn.timerconditions._ast.ASTTime;
import de.monticore.prettyprint.IndentPrinter;

public class TimerConditionsPrettyPrinter extends TimerConditionsPrettyPrinterTOP {
  
  public TimerConditionsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }
  
  @Override
  public void handle(ASTEveryTimeCondition node) {
    if (node.isPresentStart()) {
      printer.print("start ");
      handle(node.getStart());
      printer.print(", ");
    }
    if (node.isPresentTimes()) {
      printer.print(node.getTimes().getValue());
      printer.print(" times ");
    }
    printer.print("every ");
    handle(node.getPeriod());
  }
  
  @Override
  public void handle(ASTOnDateCondition node) {
    printer.print("on ");
    handle(node.getDate());
    printer.print(" ");
    handle(node.getAtTimeCondition());
  }
  
  @Override
  public void handle(ASTDate node) {
    printer.print(node.getYear().getDigits());
    printer.print("-");
    printer.print(node.getMonth().getDigits());
    printer.print("-");
    printer.print(node.getDay().getDigits());
  }
  
  @Override
  public void handle(ASTTime node) {
    printer.print(node.getHours().getDigits());
    printer.print(":");
    printer.print(node.getMinutes().getDigits());
    if (node.isPresentSeconds()) {
      printer.print(":");
      printer.print(node.getSeconds().getDigits());
    }
  }
  
}

package de.monticore.expressions.timeexpressions._ast;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import org.joda.time.LocalTime;

public class ASTTime extends ASTTimeTOP {

  public LocalTime getLocalTime() {
    return new LocalTime(
        hours.getValue(), minutes.getValue(), seconds.map(ASTNatLiteral::getValue).orElse(0));
  }
}

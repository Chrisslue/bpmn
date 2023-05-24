package de.monticore.expressions.timeexpressions.cocos;

import de.monticore.expressions.timeexpressions._cocos.TimeExpressionsCoCoChecker;

public class TemporalExpressionsCoCos {

  public static TimeExpressionsCoCoChecker createChecker() {
    TimeExpressionsCoCoChecker checker = new TimeExpressionsCoCoChecker();
    checker.addCoCo(new DateIsValidCoCo());
    checker.addCoCo(new TimeIsValidCoCo());
    return checker;
  }
}

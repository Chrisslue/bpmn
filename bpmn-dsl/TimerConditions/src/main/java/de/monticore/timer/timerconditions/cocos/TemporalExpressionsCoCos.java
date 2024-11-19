package de.monticore.timer.timerconditions.cocos;

import de.monticore.timer.timerconditions._cocos.TimerConditionsCoCoChecker;

public class TemporalExpressionsCoCos {

  public static TimerConditionsCoCoChecker createChecker() {
    TimerConditionsCoCoChecker checker = new TimerConditionsCoCoChecker();
    checker.addCoCo(new DateIsValidCoCo());
    checker.addCoCo(new TimeIsValidCoCo());
    return checker;
  }
}

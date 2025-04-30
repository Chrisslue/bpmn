package de.monticore.bpmn.timerconditions.cocos;

import de.monticore.bpmn.timerconditions._cocos.TimerConditionsCoCoChecker;

public class TemporalExpressionsCoCos {

  public static TimerConditionsCoCoChecker createChecker() {
    TimerConditionsCoCoChecker checker = new TimerConditionsCoCoChecker();
    checker.addCoCo(new DateIsValidCoCo());
    checker.addCoCo(new TimeIsValidCoCo());
    return checker;
  }
}

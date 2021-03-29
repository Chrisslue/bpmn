package de.monticore.expressions.timeexpressions.cocos;

import de.monticore.expressions.timeexpressions._cocos.TimeExpressionsCoCoChecker;

public class TemporalExpressionsCoCos {

    public static TimeExpressionsCoCoChecker createChecker() {
        return new TimeExpressionsCoCoChecker()
                .addCoCo(new DateIsValidCoCo())
                .addCoCo(new TimeIsValidCoCo());
    }

}

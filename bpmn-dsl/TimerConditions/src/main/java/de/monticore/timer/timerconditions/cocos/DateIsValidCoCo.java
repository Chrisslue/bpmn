package de.monticore.timer.timerconditions.cocos;

import com.google.common.base.Joiner;
import de.monticore.timer.Messages;
import de.monticore.timer.timerconditions._ast.ASTDate;
import de.monticore.timer.timerconditions._cocos.TimerConditionsASTDateCoCo;
import de.se_rwth.commons.logging.Log;

public class DateIsValidCoCo implements TimerConditionsASTDateCoCo {

  @Override
  public void check(final ASTDate date) {
    try {
      date.getLocalDate();
    } catch (final IllegalArgumentException e) {
      Log.error(
          Messages.TEMP.err(
              "0xTEMP04",
              Joiner.on(".")
                  .join(
                      date.getYear().getSource(),
                      date.getMonth().getSource(),
                      date.getDay().getSource())),
          date.get_SourcePositionStart());
    }
  }
}

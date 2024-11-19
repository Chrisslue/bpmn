package de.monticore.timer.timerconditions.cocos;

import com.google.common.base.Joiner;
import de.monticore.timer.Messages;
import de.monticore.timer.timerconditions._ast.ASTTime;
import de.monticore.timer.timerconditions._cocos.TimerConditionsASTTimeCoCo;
import de.se_rwth.commons.logging.Log;

public class TimeIsValidCoCo implements TimerConditionsASTTimeCoCo {

  @Override
  public void check(final ASTTime time) {
    try {
      time.getLocalTime();
    } catch (final IllegalArgumentException e) {
      String source = null;
      if (time.isPresentSeconds()) {
        source = time.getSeconds().getSource();
      }
      Log.error(
          Messages.TEMP.err(
              "0xTEMP03",
              Joiner.on(":")
                  .skipNulls()
                  .join(time.getHours().getSource(), time.getMinutes().getSource(), source)),
          time.get_SourcePositionStart());
    }
  }
}

package de.monticore.expressions.timeexpressions._ast;

import static com.cronutils.model.CronType.QUARTZ;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.parser.CronParser;
import de.monticore.expressions.Messages;
import de.se_rwth.commons.logging.Log;

public class ASTCronExpr extends ASTCronExprTOP {

  private Cron cron;

  /**
   * Sets the raw value and creates a CRON expression from the raw value.
   *
   * @param value the raw value
   */
  @Override
  public void setValue(final String value) {
    super.setValue(value);
    try {
      cron = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ)).parse(value);
    } catch (final Exception e) {
      Log.error(Messages.TEMP.err("0xTEMP02", value), get_SourcePositionStart());
    }
  }

  public Cron getCron() {
    return cron;
  }

  public String printCron() {
    return getCron().asString();
  }
}

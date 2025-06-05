package de.monticore.bpmn.timerconditions._ast;

import com.google.common.base.Strings;
import de.monticore.bpmn.TimerMessages;
import de.monticore.bpmn.timerconditions._ast.ASTPeriodTOP;
import de.se_rwth.commons.logging.Log;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joda.time.Period;

public class ASTPeriod extends ASTPeriodTOP {

  private int years = 0;
  private int months = 0;
  private int weeks = 0;
  private int days = 0;
  private int hours = 0;
  private int minutes = 0;
  private int seconds = 0;

  public void setValue(final String value) throws IllegalArgumentException {
    super.setValue(value);
    final Pattern p =
        Pattern.compile(
            "^"
                + "P"
                + "(?=\\d+[YMDW]|T\\d+[HMS])"
                + "(?:(?<years>\\d+)Y)?"
                + "(?:(?<months>\\d+)M)?"
                + "(?:(?<weeks>\\d+)W)?"
                + "(?:(?<days>\\d+)D)?"
                + "(?:T"
                + "(?:(?<hours>\\d+)H)?"
                + "(?:(?<minutes>\\d+)M)?"
                + "(?:(?<seconds>\\d+)S)?"
                + ")?"
                + "$");
    final Matcher m = p.matcher(value);
    if (!m.matches()) {
      Log.error(TimerMessages.TEMP.err("0xTEMP01", value));
    }
    try {
      years = parseInt(m.group("years"));
      months = parseInt(m.group("months"));
      weeks = parseInt(m.group("weeks"));
      days = parseInt(m.group("days"));
      hours = parseInt(m.group("hours"));
      minutes = parseInt(m.group("minutes"));
      seconds = parseInt(m.group("seconds"));
    } catch (final NumberFormatException ignored) {
    }
  }

  public Period getPeriod() {
    return new Period(years, months, weeks, days, hours, minutes, seconds, 0).normalizedStandard();
  }

  public String printISO8601() {
    return getPeriod().toString();
  }

  private int parseInt(final String string) {
    return Strings.isNullOrEmpty(string) ? 0 : Integer.parseInt(string);
  }
}

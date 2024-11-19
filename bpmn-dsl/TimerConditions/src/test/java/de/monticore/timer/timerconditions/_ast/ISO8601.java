package de.monticore.timer.timerconditions._ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import de.monticore.timer.timerconditions.AbstractTest;
import java.io.IOException;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ISO8601 extends AbstractTest {

  @Test
  void testDate() throws IOException {
    final Optional<ASTOnDateCondition> date = parser.parse_StringOnDateCondition("on 2020-02-04");

    assertEquals("2020-02-04", date.get().printISO8601());
  }

  @Test
  void testTime() throws IOException {
    final Optional<ASTAtTimeCondition> time = parser.parse_StringAtTimeCondition("at 13:37");

    assertEquals("13:37:00.000", time.get().printISO8601());
  }

  @Test
  void testAfter() throws IOException {
    final Optional<ASTAfterPeriodCondition> after =
        parser.parse_StringAfterPeriodCondition("after P3DT27H13M");

    assertEquals("P4DT3H13M", after.get().printISO8601());
  }

  @Test
  void testEvery() throws IOException {
    final Optional<ASTEveryTimeCondition> after =
        parser.parse_StringEveryTimeCondition("every PT5M");

    assertEquals("R/PT5M", after.get().printISO8601());
  }

  @Test
  void testEveryWithLimit() throws IOException {
    final Optional<ASTEveryTimeCondition> after =
        parser.parse_StringEveryTimeCondition("3 times every PT5M");

    assertEquals("R3/PT5M", after.get().printISO8601());
  }

  @Test
  void testEveryWitStart() throws IOException {
    final Optional<ASTEveryTimeCondition> after =
        parser.parse_StringEveryTimeCondition("start on 2020-02-04 at 13:37, every PT5M");

    assertEquals("R/2020-02-04T13:37:00.000/PT5M", after.get().printISO8601());
  }
}

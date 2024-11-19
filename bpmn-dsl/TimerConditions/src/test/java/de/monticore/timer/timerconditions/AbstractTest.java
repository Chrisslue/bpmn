package de.monticore.timer.timerconditions;

import de.monticore.timer.timerconditions._parser.TimerConditionsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class AbstractTest {

  protected TimerConditionsParser parser = TimerConditionsMill.parser();

  @BeforeAll
  public static void init() {
    // replacing log by a side effect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void setUp() {
    Log.getFindings().clear();
  }
}

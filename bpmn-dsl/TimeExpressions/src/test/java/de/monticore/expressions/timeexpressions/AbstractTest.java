package de.monticore.expressions.timeexpressions;

import de.monticore.expressions.timeexpressions._parser.TimeExpressionsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class AbstractTest {

  protected TimeExpressionsParser parser = TimeExpressionsMill.parser();

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

 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.base.Joiner;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** Helpers for testing CoCos. */
public class Assert {

  /**
   * Asserts that each of the expectedErrors is found at least once in the actualErrors.
   *
   * @param expectedErrors
   * @param actualErrors
   */
  public static void assertErrors(
      Collection<Finding> expectedErrors, Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (Finding expectedError : expectedErrors) {
      boolean found =
          actualErrors.stream().filter(s -> s.buildMsg().contains(expectedError.buildMsg())).count()
              >= 1;
      assertTrue(
          found,
          "The following expected error was not found: " + expectedError + actualErrorsJoined);
    }
  }

  /**
   * Asserts that each of the messages of expectedErrors is found at least once in any of the
   * actualErrors. The check omits other fields of the errors.
   *
   * @param expectedErrors
   * @param actualErrors
   */
  public static void assertErrorMsg(
      Collection<Finding> expectedErrors, Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    for (Finding expectedError : expectedErrors) {

      boolean found =
          actualErrors.stream().filter(f -> f.getMsg().equals(expectedError.getMsg())).count() >= 1;
      assertTrue(
          found,
          "The following expected error was not found: " + expectedError + actualErrorsJoined);
    }
  }

  /**
   * Asserts that there are exactly as many actual errors as expected.
   *
   * @param expectedErrors
   * @param actualErrors
   */
  public static void assertEqualErrorCounts(
      Collection<Finding> expectedErrors, Collection<Finding> actualErrors) {
    String actualErrorsJoined = "\nactual Errors: \n\t" + Joiner.on("\n\t").join(actualErrors);
    String expectedErrorsJoined =
        "\nexpected Errors: \n\t" + Joiner.on("\n\t").join(expectedErrors);
    assertEquals(
        expectedErrors.size(),
        actualErrors.size(),
        "Expected "
            + expectedErrors.size()
            + " errors, but found "
            + actualErrors.size()
            + "."
            + expectedErrorsJoined
            + actualErrorsJoined);
  }

  public static void assertHasErrorCode(String code) {
    assertTrue(
        getAllErrorCodes().stream().anyMatch(code::equals),
        "Error \""
            + code
            + "\" expected, "
            + "but instead the errors are:"
            + System.lineSeparator()
            + Log.getFindings().stream()
                .map(Finding::buildMsg)
                .collect(Collectors.joining(System.lineSeparator()))
            + System.lineSeparator());
  }

  protected static List<String> getFirstErrorCodes(long n) {
    List<String> errorsInLog =
        Log.getFindings().stream()
            .filter(Finding::isError)
            .map(err -> err.getMsg().split(" ")[0])
            .limit(n)
            .collect(Collectors.toList());
    List<String> errorsToReturn;

    if (errorsInLog.size() < n) {
      errorsToReturn = errorsInLog;
      for (int i = 0; i < n - errorsInLog.size(); i++) {
        errorsToReturn.add("");
      }
    } else {
      errorsToReturn = errorsInLog.subList(0, (int) n);
    }
    return errorsToReturn;
  }

  protected static List<String> getAllErrorCodes() {
    return getFirstErrorCodes(Log.getErrorCount());
  }
}

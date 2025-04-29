 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.cocos.types;

import com.google.common.collect.Lists;
import de.monticore.bpmn.cocos.AbstractCoCoTest;
import de.monticore.bpmn.workflow._cocos.WorkflowCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import java.util.Collection;
import org.junit.jupiter.api.Test;

public class TypeExists extends AbstractCoCoTest {

  @Override
  protected WorkflowCoCoChecker getChecker() {
    // empty checker here, since error is raised during symbol table creation
    return new WorkflowCoCoChecker();
  }

  @Test
  void taskContainsNonBoundaryEvents() {
    String modelName = "de.monticore.bpmn.cocos.types.invalid.TypeExists";

    Collection<Finding> expectedErrors =
        Lists.newArrayList(Finding.error("0xA0324 Cannot find symbol FooDoesNotExist"));

    testModelForErrors(modelName, expectedErrors);
  }

  @Test
  void taskContainsOnlyBoundaryEvents() {
    String modelName = "de.monticore.bpmn.cocos.types.valid.TypeExists";

    testModelNoErrors(modelName);
  }
}

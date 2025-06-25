package de.monticore.bpmn.tool;

import de.monticore.bpmn.AbstractTest;
import de.monticore.bpmn.workflow._ast.ASTWorkflowCompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class BPMNWorkflowToolTest extends AbstractTest {

    @Test
    public void testTool() {
        BPMNWorkflowTool.main(new String[]{"-i", "src/test/resources/de/monticore/bpmn/tool/BPMNWorkflowToolTest.wfm"});
    }
    
    @Test
    public void testToolWithPrettyPrint() {
        //ToDo: add pretty print test
    }
  
}

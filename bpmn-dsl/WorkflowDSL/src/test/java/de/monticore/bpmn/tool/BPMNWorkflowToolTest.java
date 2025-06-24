package de.monticore.bpmn.tool;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BPMNWorkflowToolTest {

    private String model;

    @BeforeEach
    public void setup() {
        String path = "src/test/resources/de/monticore/bpmn/tool/";

        model = path + "BPMNWorkflowToolTest.wfm";

    }

    @Test
    public void testPossibleCommand() {
        // given
        String[] workflow = new String[] {"-i", model};

        // when
        BPMNWorkflowTool oclTool = new BPMNWorkflowTool();
        oclTool.run(workflow);
    }
}

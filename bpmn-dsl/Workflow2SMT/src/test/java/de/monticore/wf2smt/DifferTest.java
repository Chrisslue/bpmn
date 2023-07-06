package de.monticore.wf2smt;

import de.monticore.wf2lts.LTSTestingUtils;
import de.monticore.wf2lts.datastructure.LTS;
import java.util.List;
import org.junit.jupiter.api.Test;

class DifferTest {

  @Test
  void findWitness() {

    var first = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("A", "B", "C"));
    LTSTestingUtils.addPathOfLabelFromStart(first, List.of("D", "E", "End"));
    var second = new LTS();
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("A", "B", "C", "End"));
    LTSTestingUtils.addPathOfLabelFromStart(second, List.of("D", "E", "End"));

    var differ = new Differ(second, first);
    differ.findWitness(10);
  }
}
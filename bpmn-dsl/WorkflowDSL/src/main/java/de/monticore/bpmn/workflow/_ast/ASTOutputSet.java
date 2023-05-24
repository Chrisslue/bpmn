package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.Sets;
import java.util.Collection;

public class ASTOutputSet extends ASTOutputSetTOP {

  // expected input sets for this output set to be produced
  private final Collection<ASTInputSet> expectedInputSets = Sets.newHashSet();

  public Collection<ASTInputSet> getExpectedInputSets() {
    return expectedInputSets;
  }

  public void addExpectedInputSet(final ASTInputSet inputSet) {
    expectedInputSets.add(inputSet);
  }

  public void addAllExpectedInputSets(final Collection<ASTInputSet> inputSets) {
    expectedInputSets.addAll(inputSets);
  }
}

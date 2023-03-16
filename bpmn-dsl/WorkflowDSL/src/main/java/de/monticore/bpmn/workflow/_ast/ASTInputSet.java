package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTInputSet extends ASTInputSetTOP {

    // expected output sets to be produced for this input set
    private final Collection<ASTOutputSet> expectedOutputSets = Sets.newHashSet();

    public Collection<ASTOutputSet> getExpectedOutputSets() {
        return expectedOutputSets;
    }

    public void addExpectedOutputSet(final ASTOutputSet outputSet) {
        expectedOutputSets.add(outputSet);
    }

    public void addAllExpectedOutputSets(final Collection<ASTOutputSet> outputSets) {
        expectedOutputSets.addAll(outputSets);
    }

}

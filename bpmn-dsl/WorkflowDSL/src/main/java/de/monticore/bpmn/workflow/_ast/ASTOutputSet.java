package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ASTOutputSet extends ASTOutputSetTOP {

    // expected input sets for this output set to be produced
    private final Collection<ASTInputSet> expectedInputSets = Sets.newHashSet();

    protected ASTOutputSet() {
        super();
    }

    protected ASTOutputSet (
            Optional<ASTDataIO> output,
            List<ASTDataIO> outputs
    ) {
        super(Optional.empty(), output.isPresent() ? Lists.newArrayList(output.get()) : outputs);
    }

    @Override
    public ASTDataIO getOutput() {
        throw new UnsupportedOperationException("Use ASTOutputSet#getOutputList instead");
    }

    @Override
    public void setOutputOpt(final Optional<ASTDataIO> outputOpt) {
        outputOpt.ifPresent(output -> outputs.add(output));
    }

    @Override
    public void setOutput(final ASTDataIO output) {
        outputs.add(output);
    }

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

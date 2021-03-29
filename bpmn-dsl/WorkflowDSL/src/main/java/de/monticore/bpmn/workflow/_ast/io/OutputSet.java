package de.monticore.bpmn.workflow._ast.io;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.ASTOutputSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Data output set.
 */
public class OutputSet {

    private final List<ASTOutputSet> astOutputSets = Lists.newArrayList();

    private final Set<InputSet> expectedInputSets = Sets.newHashSet();

    private final Set<DataOutput> dataOutputs = Sets.newHashSet();
    private final Set<DataOutput> dataOutputsOptional = Sets.newHashSet();
    private final Set<DataOutput> dataOutputsExecute = Sets.newHashSet();

    public Collection<ASTOutputSet> getAstOutputSets() {
        return Collections.unmodifiableList(astOutputSets);
    }

    public Collection<InputSet> getExpectedInputSets() {
        return Collections.unmodifiableSet(expectedInputSets);
    }

    public Collection<DataOutput> getDataOutputs() {
        return Collections.unmodifiableSet(dataOutputs);
    }

    public Collection<DataOutput> getDataOutputsOptional() {
        return Collections.unmodifiableSet(dataOutputsOptional);
    }

    public Collection<DataOutput> getDataOutputsExecute() {
        return Collections.unmodifiableSet(dataOutputsExecute);
    }

    public Stream<DataOutput> streamDataOutputs() {
        return dataOutputs.stream();
    }

    public Stream<DataOutput> streamDataOutputsOptional() {
        return dataOutputsOptional.stream();
    }

    public Stream<DataOutput> streamDataOutputsExecute() {
        return dataOutputsExecute.stream();
    }

    public boolean addAstOutputSet(final ASTOutputSet outputSet) {
        return astOutputSets.add(outputSet);
    }

    public boolean addAllAstOutputSets(final Collection<ASTOutputSet> outputSets) {
        return astOutputSets.addAll(outputSets);
    }

    public boolean addExpectedInputSet(final InputSet inputSet) {
        return expectedInputSets.add(inputSet);
    }

    public boolean addAllExpectedInputSet(final Collection<InputSet> inputSets) {
        return expectedInputSets.addAll(inputSets);
    }

    public boolean addDataOutput(final DataOutput output) {
        return dataOutputs.add(output);
    }

    public boolean addAllDataOutput(final Collection<DataOutput> outputs) {
        return dataOutputs.addAll(outputs);
    }

    public boolean addDataOutputOptional(final DataOutput output) {
        return dataOutputsOptional.add(output);
    }

    public boolean addAllDataOutputOptional(final Collection<DataOutput> outputs) {
        return dataOutputsOptional.addAll(outputs);
    }

    public boolean addDataOutputExecute(final DataOutput output) {
        return dataOutputsExecute.add(output);
    }

    public boolean addAllDataOutputExecute(final Collection<DataOutput> outputs) {
        return dataOutputsExecute.addAll(outputs);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OutputSet
                && getDataOutputs().equals(((OutputSet) obj).getDataOutputs())
                && getDataOutputsOptional().equals(((OutputSet) obj).getDataOutputsOptional())
                && getDataOutputsExecute().equals(((OutputSet) obj).getDataOutputsExecute());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getDataOutputs(), getDataOutputsOptional(), getDataOutputsExecute());
    }

}

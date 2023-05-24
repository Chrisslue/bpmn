package de.monticore.bpmn.workflow._ast.io;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.ASTInputSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/** Data input set. */
public class InputSet {

  private final List<ASTInputSet> astInputSets = Lists.newArrayList();

  private final Set<OutputSet> expectedOutputSets = Sets.newHashSet();

  private final Set<DataInput> dataInputs = Sets.newHashSet();
  private final Set<DataInput> dataInputsOptional = Sets.newHashSet();
  private final Set<DataInput> dataInputsExecute = Sets.newHashSet();

  public final Collection<ASTInputSet> getAstInputSets() {
    return Collections.unmodifiableList(astInputSets);
  }

  public Collection<OutputSet> getExpectedOutputSets() {
    return Collections.unmodifiableSet(expectedOutputSets);
  }

  public Collection<DataInput> getDataInputs() {
    return Collections.unmodifiableSet(dataInputs);
  }

  public Collection<DataInput> getDataInputsOptional() {
    return Collections.unmodifiableSet(dataInputsOptional);
  }

  public Collection<DataInput> getDataInputsExecute() {
    return Collections.unmodifiableSet(dataInputsExecute);
  }

  public Stream<DataInput> streamDataInputs() {
    return dataInputs.stream();
  }

  public Stream<DataInput> streamDataInputsOptional() {
    return dataInputsOptional.stream();
  }

  public Stream<DataInput> streamDataInputsExecute() {
    return dataInputsExecute.stream();
  }

  public boolean addAstInputSet(final ASTInputSet inputSet) {
    return astInputSets.add(inputSet);
  }

  public boolean addAllAstOutputSets(final Collection<ASTInputSet> inputSets) {
    return astInputSets.addAll(inputSets);
  }

  public boolean addExpectedOutputSet(final OutputSet outputSet) {
    return expectedOutputSets.add(outputSet);
  }

  public boolean addAllExpectedOutputSet(final Collection<OutputSet> outputSets) {
    return expectedOutputSets.addAll(outputSets);
  }

  public boolean addDataInput(final DataInput input) {
    return dataInputs.add(input);
  }

  public boolean addAllDataInput(final Collection<DataInput> inputs) {
    return dataInputs.addAll(inputs);
  }

  public boolean addDataInputOptional(final DataInput input) {
    return dataInputsOptional.add(input);
  }

  public boolean addAllDataInputOptional(final Collection<DataInput> inputs) {
    return dataInputsOptional.addAll(inputs);
  }

  public boolean addDataInputExecute(final DataInput input) {
    return dataInputsExecute.add(input);
  }

  public boolean addAllDataInputExecute(final Collection<DataInput> inputs) {
    return dataInputsExecute.addAll(inputs);
  }

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof InputSet
        && getDataInputs().equals(((InputSet) obj).getDataInputs())
        && getDataInputsOptional().equals(((InputSet) obj).getDataInputsOptional())
        && getDataInputsExecute().equals(((InputSet) obj).getDataInputsExecute());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getDataInputs(), getDataInputsOptional(), getDataInputsExecute());
  }
}

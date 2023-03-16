package de.monticore.bpmn.workflow._ast.io;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.ASTDataIO;
import de.monticore.bpmn.workflow._ast.ASTDataObject;
import de.monticore.bpmn.workflow._symboltable.DataObjectSymbol;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Data output.
 */
public class DataOutput implements DataInputOutput {

    private final String name;
    private final boolean isCollection;

    private final Collection<ASTDataIO> astDataOutputs = Lists.newArrayList();
    private final Collection<OutputSet> outputSets = Sets.newHashSet();
    private final Collection<OutputSet> outputSetsOptional = Sets.newHashSet();
    private final Collection<OutputSet> outputSetsExecute = Sets.newHashSet();
    private Optional<DataObjectSymbol> targetSymbol;

    public DataOutput(final String name, boolean isCollection) {
        this.name = name;
        this.isCollection = isCollection;
    }

    private Optional<DataObjectSymbol> getTargetSymbol() {
        return targetSymbol;
    }

    void setTargetSymbol(DataObjectSymbol item) {
        targetSymbol = Optional.of(item);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ASTMCType getMCType() {
        return getTargetSymbol()
                .map(DataObjectSymbol::getAstNode)
                .map(ASTDataObject::getMCType)
                .orElse(null);
    }

    @Override
    public boolean isCollection() {
        return isCollection;
    }

    public Collection<ASTDataIO> getAstDataOutputs() {
        return Collections.unmodifiableCollection(astDataOutputs);
    }

    public boolean addASTDataOutput(final ASTDataIO astOutput) {
        return astDataOutputs.add(astOutput);
    }

    public boolean addAllASTDataOutput(final Collection<ASTDataIO> astOutputs) {
        return astDataOutputs.addAll(astOutputs);
    }

    public Collection<OutputSet> getOutputSets() {
        return Collections.unmodifiableCollection(outputSets);
    }

    public Collection<OutputSet> getOutputSetsOptional() {
        return Collections.unmodifiableCollection(outputSetsOptional);
    }

    public Collection<OutputSet> getOutputSetsExecute() {
        return Collections.unmodifiableCollection(outputSetsExecute);
    }

    public boolean addOutputSet(final OutputSet outputSet) {
        return outputSets.add(outputSet);
    }

    public boolean addAllOutputSet(final Collection<OutputSet> outputSets) {
        return this.outputSets.addAll(outputSets);
    }

    public boolean addOutputSetOptional(final OutputSet outputSet) {
        return outputSetsOptional.add(outputSet);
    }

    public boolean addAllOutputSetOptional(final Collection<OutputSet> outputSets) {
        return outputSetsOptional.addAll(outputSets);
    }

    public boolean addOutputSetExecute(final OutputSet outputSet) {
        return outputSetsExecute.add(outputSet);
    }

    public boolean addAllOutputSetExecute(final Collection<OutputSet> outputSets) {
        return outputSetsExecute.addAll(outputSets);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DataOutput && getName().equals(((DataOutput) obj).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    public ASTCDParameter asParameter() {
        return CD4CodeMill.cDParameterBuilder().setName(getName()).setMCType(getMCType()).build();
    }

}

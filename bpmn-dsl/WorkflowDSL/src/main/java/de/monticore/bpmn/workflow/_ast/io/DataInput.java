package de.monticore.bpmn.workflow._ast.io;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.ASTDataIO;
import de.monticore.bpmn.workflow._ast.ASTDataObject;
import de.monticore.bpmn.workflow._symboltable.DataObjectSymbol;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDParameter;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisMill;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * Data input.
 */
public class DataInput implements DataInputOutput {

    private final String name;
    private final boolean isCollection;

    private final Collection<ASTDataIO> astDataInputs = Lists.newArrayList();
    private final Collection<InputSet> inputSets = Sets.newHashSet();
    private final Collection<InputSet> inputSetsOptional = Sets.newHashSet();
    private final Collection<InputSet> inputSetsExecute = Sets.newHashSet();
    private Optional<DataObjectSymbol> sourceSymbol = Optional.empty();

    public DataInput(final String name, final boolean isCollection) {
        this.name = name;
        this.isCollection = isCollection;
    }

    private Optional<DataObjectSymbol> getSourceSymbol() {
        return sourceSymbol;
    }

    void setSourceSymbol(final DataObjectSymbol item) {
        sourceSymbol = Optional.of(item);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ASTType getType() {
        return getSourceSymbol()
                .map(DataObjectSymbol::getDataObjectNode)
                .map(Optional::get)
                .map(ASTDataObject::getType)
                .orElse(null);
    }

    @Override
    public boolean isCollection() {
        return isCollection;
    }

    @Override
    public Optional<DataObjectSymbol> getReferencedDataObject() {
        return getSourceSymbol();
    }

    public Collection<ASTDataIO> getAstDataInputs() {
        return Collections.unmodifiableCollection(astDataInputs);
    }

    public boolean addASTDataInput(final ASTDataIO astInput) {
        return astDataInputs.add(astInput);
    }

    public boolean addAllASTDataInput(final Collection<ASTDataIO> astInputs) {
        return astDataInputs.addAll(astInputs);
    }

    public Collection<InputSet> getInputSets() {
        return Collections.unmodifiableCollection(inputSets);
    }

    public Collection<InputSet> getInputSetsOptional() {
        return Collections.unmodifiableCollection(inputSetsOptional);
    }

    public Collection<InputSet> getInputSetsExecute() {
        return Collections.unmodifiableCollection(inputSetsExecute);
    }

    public boolean addInputSet(final InputSet inputSet) {
        return inputSets.add(inputSet);
    }

    public boolean addAllInputSet(final Collection<InputSet> inputSets) {
        return this.inputSets.addAll(inputSets);
    }

    public boolean addInputSetOptional(final InputSet inputSet) {
        return inputSetsOptional.add(inputSet);
    }

    public boolean addAllInputSetOptional(final Collection<InputSet> inputSets) {
        return inputSetsOptional.addAll(inputSets);
    }

    public boolean addInputSetExecute(final InputSet inputSet) {
        return inputSetsExecute.add(inputSet);
    }

    public boolean addAllInputSetExecute(final Collection<InputSet> inputSets) {
        return inputSetsExecute.addAll(inputSets);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DataInput && getName().equals(((DataInput) obj).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getName());
    }

    public ASTCDParameter asParameter() {
        return CD4AnalysisMill.cDParameterBuilder().setName(getName()).setType(getType()).build();
    }

}
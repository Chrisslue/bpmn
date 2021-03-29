package de.monticore.bpmn.workflow._ast.io;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._symboltable.DataObjectSymbol;
import de.monticore.bpmn.workflow._visitor.WorkflowVisitor;
import de.monticore.symboltable.Scope;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * IO specification.
 */
public class IOSpecification {

    private Collection<InputSet> inputSets;

    private Collection<OutputSet> outputSets;

    private Collection<DataInput> inputs;

    private Collection<DataOutput> outputs;

    private IOSpecification(
            final Collection<InputSet> inputSets,
            final Collection<OutputSet> outputSets,
            final Collection<DataInput> inputs,
            final Collection<DataOutput> outputs
    ) {
        this.inputSets = inputSets;
        this.outputSets = outputSets;
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /*
    (There might be multiple AST nodes denoting the same data input/output or the same data input/output set.)
    Construct sets of unique data inputs/outputs and input/output sets, and add references in both directions.
     */
    public static IOSpecification from(final ASTIOSpecification ast, final Scope enclosingScope) {
        final Map<DataInput, DataInput> inputs = Maps.newHashMap();
        final Map<DataOutput, DataOutput> outputs = Maps.newHashMap();

        final Map<InputSet, InputSet> inputSets = Maps.newHashMap();
        final Map<OutputSet, OutputSet> outputSets = Maps.newHashMap();

        ast.accept(new WorkflowVisitor() {
            @Override
            public void visit(final ASTIRequirement inputRequirement) {
                handleInputSet(inputRequirement.getInputSet());
            }

            @Override
            public void visit(final ASTORequirement outputRequirement) {
                handleOutputSet(outputRequirement.getOutputSet());
            }

            @Override
            public void visit(final ASTIORule ioRule) {
                final InputSet inputSet = handleInputSet(ioRule.getInputSet());
                final OutputSet outputSet = handleOutputSet(ioRule.getOutputSet());
                inputSet.addExpectedOutputSet(outputSet);
                outputSet.addExpectedInputSet(inputSet);
            }

            private InputSet handleInputSet(final ASTInputSet astInputSet) {
                final InputSet inputSetCandidate = new InputSet();

                astInputSet.getInputList().forEach(astInput -> {
                    final String inputName = astInput.getQName().getQualifiedName();
                    final DataInput inputCandidate = new DataInput(inputName, false);

                    enclosingScope.<DataObjectSymbol>resolve(inputName, DataObjectSymbol.KIND).ifPresent(inputCandidate::setSourceSymbol);

                    if (!inputs.containsKey(inputCandidate)) { // the same data input does not yet exist
                        inputs.put(inputCandidate, inputCandidate);
                    }
                    final DataInput input = inputs.get(inputCandidate);
                    input.addASTDataInput(astInput);

                    inputSetCandidate.addDataInput(input);
                    if (astInput.isOptional()) {
                        inputSetCandidate.addDataInputOptional(input);
                    }
                    if (astInput.isWhileExecuting()) {
                        inputSetCandidate.addDataInputExecute(input);
                    }
                });

                if (!inputSets.containsKey(inputSetCandidate)) { // the same input set w/ the same data inputs does not yet exist
                    inputSetCandidate.getDataInputs()
                            .forEach(in -> in.addInputSet(inputSetCandidate));
                    inputSetCandidate.getDataInputsOptional()
                            .forEach(in -> in.addInputSetOptional(inputSetCandidate));
                    inputSetCandidate.getDataInputsExecute()
                            .forEach(in -> in.addInputSetExecute(inputSetCandidate));

                    inputSets.put(inputSetCandidate, inputSetCandidate);
                }
                final InputSet inputSet = inputSets.get(inputSetCandidate);
                inputSet.addAstInputSet(astInputSet);

                return inputSet;
            }

            private OutputSet handleOutputSet(final ASTOutputSet astOutputSet) {
                final OutputSet outputSetCandidate = new OutputSet();

                astOutputSet.getOutputList().forEach(astOutput -> {
                    final String outputName = astOutput.getQName().getQualifiedName();
                    final DataOutput outputCandidate = new DataOutput(outputName, false);

                    enclosingScope.<DataObjectSymbol>resolve(outputName, DataObjectSymbol.KIND)
                            .ifPresent(outputCandidate::setTargetSymbol);

                    if (!outputs.containsKey(outputCandidate)) { // the same data output does not yet exist
                        outputs.put(outputCandidate, outputCandidate);
                    }
                    final DataOutput output = outputs.get(outputCandidate);
                    output.addASTDataOutput(astOutput);

                    outputSetCandidate.addDataOutput(output);
                    if (astOutput.isOptional()) {
                        outputSetCandidate.addDataOutputOptional(output);
                    }
                    if (astOutput.isWhileExecuting()) {
                        outputSetCandidate.addDataOutputExecute(output);
                    }
                });

                if (!outputSets.containsKey(outputSetCandidate)) { // the same output set w/ the same data outputs does not yet exist
                    outputSetCandidate.getDataOutputs()
                            .forEach(output -> output.addOutputSet(outputSetCandidate));
                    outputSetCandidate.getDataOutputsOptional()
                            .forEach(output -> output.addOutputSetOptional(outputSetCandidate));
                    outputSetCandidate.getDataOutputsExecute()
                            .forEach(output -> output.addOutputSetExecute(outputSetCandidate));

                    outputSets.put(outputSetCandidate, outputSetCandidate);
                }
                final OutputSet outputSet = outputSets.get(outputSetCandidate);
                outputSet.addAstOutputSet(astOutputSet);

                return outputSet;
            }
        });

        return new IOSpecification(inputSets.values(), outputSets.values(), inputs.values(), outputs.values());
    }

    public Collection<InputSet> getInputSets() {
        return Sets.newHashSet(inputSets);
    }

    public Collection<OutputSet> getOutputSets() {
        return Sets.newHashSet(outputSets);
    }

    public Collection<DataInput> getInputs() {
        return Sets.newHashSet(inputs);
    }

    public Collection<DataOutput> getOutputs() {
        return Sets.newHashSet(outputs);
    }

}

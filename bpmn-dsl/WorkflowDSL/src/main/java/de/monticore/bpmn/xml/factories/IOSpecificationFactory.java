package de.monticore.bpmn.xml.factories;

import static de.se_rwth.commons.StringTransformations.uncapitalize;

import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import de.monticore.bpmn.workflow._ast.io.*;
import jakarta.xml.bind.JAXBElement;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.omg.spec.bpmn._20100524.model.*;

class IOSpecificationFactory {

  private static final ObjectFactory factory = new ObjectFactory();

  public static TInputOutputSpecification makeXml(
      final IOSpecification ioSpec, final String prefix) {
    return new IOSpecificationFactory().buildXml(ioSpec, prefix);
  }

  private TInputOutputSpecification buildXml(final IOSpecification ioSpec, final String prefix) {
    final Collection<TDataInput> inputs =
        ioSpec.getInputs().stream()
            .map(i -> buildXmlDataInput(i, prefix))
            .collect(Collectors.toList());
    final Collection<TDataOutput> outputs =
        ioSpec.getOutputs().stream()
            .map(o -> buildXmlDataOutput(o, prefix))
            .collect(Collectors.toList());

    final Collection<TInputSet> inputSets =
        ioSpec.getInputSets().stream()
            .map(i -> buildXmlInputSet(i, prefix))
            .collect(Collectors.toList());
    if (inputSets.isEmpty()) {
      inputSets.add(factory.createTInputSet());
    }
    final Collection<TOutputSet> outputSets =
        ioSpec.getOutputSets().stream()
            .map(o -> buildXmlOutputSet(o, prefix))
            .collect(Collectors.toList());
    if (outputSets.isEmpty()) {
      outputSets.add(factory.createTOutputSet());
    }

    final TInputOutputSpecification t = factory.createTInputOutputSpecification();
    t.getDataInput().addAll(inputs);
    t.getDataOutput().addAll(outputs);
    t.getInputSet().addAll(inputSets);
    t.getOutputSet().addAll(outputSets);

    return t;
  }

  private TInputSet buildXmlInputSet(InputSet inputSet, String prefix) {
    TInputSet t = factory.createTInputSet();
    buildXmlInputRefs(
        inputSet::getDataInputs,
        prefix,
        factory::createTInputSetDataInputRefs,
        t::getDataInputRefs);
    buildXmlInputRefs(
        inputSet::getDataInputs,
        prefix,
        factory::createTInputSetDataInputRefs,
        t::getOptionalInputRefs);
    buildXmlInputRefs(
        inputSet::getDataInputs,
        prefix,
        factory::createTInputSetDataInputRefs,
        t::getWhileExecutingInputRefs);

    return t;
  }

  private TOutputSet buildXmlOutputSet(OutputSet outputSet, String prefix) {
    TOutputSet t = factory.createTOutputSet();
    buildXmlOutputRefs(
        outputSet::getDataOutputs,
        prefix,
        factory::createTOutputSetDataOutputRefs,
        t::getDataOutputRefs);
    buildXmlOutputRefs(
        outputSet::getDataOutputsOptional,
        prefix,
        factory::createTOutputSetOptionalOutputRefs,
        t::getOptionalOutputRefs);
    buildXmlOutputRefs(
        outputSet::getDataOutputsExecute,
        prefix,
        factory::createTOutputSetWhileExecutingOutputRefs,
        t::getWhileExecutingOutputRefs);

    return t;
  }

  private TDataInput buildXmlDataInput(DataInput dataInput, String idPrefix) {
    TDataInput tDataInput = factory.createTDataInput();
    tDataInput.setId(Joiner.on("-").join(uncapitalize(idPrefix), "input", dataInput.getName()));
    tDataInput.setName(dataInput.getName());
    /*
    if (dataInput.isPresentType()) {
        tDataInput.setItemSubjectRef(new QName(dataInput.getType().toString()));
        tDataInput.setIsCollection(TypesHelper.isCollection(dataInput.getType().toString()));
    }
    */
    return tDataInput;
  }

  private TDataOutput buildXmlDataOutput(DataOutput dataOutput, String idPrefix) {
    TDataOutput tDataOutput = factory.createTDataOutput();
    tDataOutput.setId(Joiner.on("-").join(uncapitalize(idPrefix), "output", dataOutput.getName()));
    tDataOutput.setName(dataOutput.getName());
    /*
    if (dataOutput.isPresentType()) {
        tDataOutput.setItemSubjectRef(new QName(dataOutput.getType().toString()));
        tDataOutput.setIsCollection(TypesHelper.isCollection(dataOutput.getType().toString()));
    }
    */
    return tDataOutput;
  }

  private void buildXmlInputRefs(
      final Supplier<? extends Collection<DataInput>> inputs,
      final String prefix,
      final Function<TDataInput, JAXBElement<Object>> serializer,
      final Supplier<? extends List<JAXBElement<Object>>> target) {
    buildXmlIOSet(inputs, prefix, this::buildXmlDataInput, serializer, target);
  }

  private void buildXmlOutputRefs(
      final Supplier<? extends Collection<DataOutput>> outputs,
      final String prefix,
      final Function<TDataOutput, JAXBElement<Object>> serializer,
      final Supplier<? extends List<JAXBElement<Object>>> target) {
    buildXmlIOSet(outputs, prefix, this::buildXmlDataOutput, serializer, target);
  }

  private <T extends DataInputOutput, U> void buildXmlIOSet(
      final Supplier<? extends Collection<T>> inputs,
      final String prefix,
      final BiFunction<T, String, U> serializer1,
      final Function<U, JAXBElement<Object>> serializer2,
      final Supplier<? extends List<JAXBElement<Object>>> target) {
    target
        .get()
        .addAll(
            inputs.get().stream()
                .map(input -> serializer1.apply(input, prefix))
                .map(serializer2)
                .collect(Collectors.toList()));
  }
}

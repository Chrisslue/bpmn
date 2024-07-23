package de.monticore.bpmn;

import java.util.List;

public class Resources {

  private Resources() {}

  public static final String bpmnLocation = "src/test/resources/de/monticore/bpmn/";

  public static final String CYCLIC = bpmnLocation + "wf2lts/Cyclic.wfm";
  public static final String CYCLIC_GATEWAY = bpmnLocation + "wf2lts/CyclicGateway.wfm";
  public static final String NESTED_GATEWAY = bpmnLocation + "wf2lts/NestedGateway.wfm";
  public static final String MERGING_AND_END_GATEWAY =
      bpmnLocation + "wf2lts/transformer/MergingAndEndGateway.wfm";

  public static final String MULTIPLE_INCOMING_OUTGOING =
      bpmnLocation + "wf2lts/transformer/MultipleIncomingOutgoing.wfm";
  public static final String NO_MERGING_GATEWAY =
      bpmnLocation + "wf2lts/transformer/NoMergingGateway.wfm";
  public static final String WITH_MERGING_GATEWAY =
      bpmnLocation + "wf2lts/transformer/WithMergingGateway.wfm";
  public static final String SIMPLE = bpmnLocation + "wf2smt/Simple.wfm";

  public static final String SIMPLE_EQUIVALENT = bpmnLocation + "wf2smt/SimpleEquivalent.wfm";
  public static final String SIMPLE_NOT_EQUIVALENT =
      bpmnLocation + "wf2smt/SimpleNotEquivalent.wfm";
  public static final String PROTOTYPE = bpmnLocation + "Prototype.wfm";

  public static List<String> allValidModel() {
    return List.of(
        CYCLIC,
        CYCLIC_GATEWAY,
        NESTED_GATEWAY,
        MERGING_AND_END_GATEWAY,
        MULTIPLE_INCOMING_OUTGOING,
        NO_MERGING_GATEWAY,
        WITH_MERGING_GATEWAY,
        SIMPLE,
        SIMPLE_EQUIVALENT,
        SIMPLE_NOT_EQUIVALENT,
        PROTOTYPE);
  }
}

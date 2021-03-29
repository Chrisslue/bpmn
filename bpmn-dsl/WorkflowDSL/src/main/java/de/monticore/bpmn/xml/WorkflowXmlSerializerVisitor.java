package de.monticore.bpmn.xml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.monticore.bpmn.collectors.WorkflowCollectors;
import de.monticore.bpmn.visitors.WorkflowLocalInheritanceVisitor;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceVisitor;
import de.monticore.bpmn.xml.factories.*;
import org.omg.spec.bpmn._20100524.model.*;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Builds the BPMN 2.0 XML content tree from BPMN models.
 *
 * Extend this class to add vendor-specific or your own extensions, attributes, etc.
 *
 * @see WorkflowXmlSerializer
 */

// TODO include data and IO in XML output (mapping not completely clear, ignored by Camunda and other engines anyways ...)
// TODO improvement: use a (configurable) ID provider and inject it into factories/factory calls.

public class WorkflowXmlSerializerVisitor extends WorkflowLocalInheritanceVisitor {

    private final List<JAXBElement<? extends TRootElement>> rootElements = Lists.newArrayList();
    private final List<JAXBElement<? extends TFlowElement>> flowElements = Lists.newArrayList();
    private final List<JAXBElement<? extends TArtifact>> artifacts = Lists.newArrayList();

    // process and sub-process are completely unrelated on the XML-side
    protected JAXBElement<TProcess> xmlProcess = null;
    protected JAXBElement<? extends TSubProcess> xmlSubProcess = null;

    private final Map<ASTFlowNode, TFlowNode> flowNodeMap = Maps.newHashMap();

    protected Stack<TLane> laneStack = new Stack<>();
    protected TLaneSet xmlRootLaneSet = null;

    public WorkflowXmlSerializerVisitor(final ASTFlowElementContainer root) {
        super(root);
    }

    protected void makeXml() {
        localRoot.accept(getRealThis());
    }

    // Concrete activity type is handled further down.
    @Override
    public void visit(final ASTActivity activity) {
        if (activity.isPresentCompensationHandler()) {
            final QName source = CommonFactory.makeQName(activity.getName());
            final QName target = CommonFactory.makeQName(activity.getCompensationHandler().getActivity());
            final JAXBElement<TAssociation> xmlNode = CommonFactory.makeXmlAssociation(source, target);

            addArtifact(xmlNode);
        }
    }

    @Override
    public void visit(final ASTTask task) {
        final JAXBElement<? extends TActivity> xmlNode = AtomicActivityFactory.makeXml(task);

        addFlowNode(task, xmlNode);
    }

    @Override
    public void visit(final ASTCallActivity callActivity) {
        JAXBElement<? extends TActivity> xmlNode = AtomicActivityFactory.makeXml(callActivity);

        addFlowNode(callActivity, xmlNode);
    }

    @Override
    public void visit(final ASTGateway gateway) {
        JAXBElement<? extends TGateway> xmlNode = GatewayFactory.makeXml(gateway);

        addFlowNode(gateway, xmlNode);
    }

    @Override
    public void visit(final ASTEvent event) {
        JAXBElement<? extends TEvent> xmlNode = EventFactory.makeXml(event);

        addFlowNode(event, xmlNode);
    }

    @Override
    public void visit(final ASTProcess process) {
        xmlProcess = CommonFactory.makeXmlProcess(process);
    }

    /**
     * Do not add sub-process as flow-node. Done by recursion scheme
     * @see #endVisit(ASTSubProcess)
     * @param subProcess
     */
    @Override
    public void visit(final ASTSubProcess subProcess) {
        xmlSubProcess = SubProcessFactory.makeXml(subProcess);
    }

    /**
     * Override with care
     * @param process
     */
    @Override
    public void endVisit(final ASTProcess process) {
        if (process == localRoot) {
            handleSequenceFlow(process);

            TProcess tProcess = xmlProcess.getValue();
            if (null != xmlRootLaneSet) {
                tProcess.getLaneSet().add(xmlRootLaneSet);
            }
            tProcess.getFlowElement().addAll(flowElements);
            tProcess.getArtifact().addAll(artifacts);
            // also add process to root elements
            rootElements.add(xmlProcess);
        }
    }

    /**
     * Override with care
     * @param subProcess
     */
    @Override
    public void endVisit(final ASTSubProcess subProcess) {
        if (subProcess == localRoot) {
            handleSequenceFlow(subProcess);

            TSubProcess tSubProcess = xmlSubProcess.getValue();
            if (null != xmlRootLaneSet) {
                tSubProcess.getLaneSet().add(xmlRootLaneSet);
            }
            tSubProcess.getFlowElement().addAll(flowElements);
            tSubProcess.getArtifact().addAll(artifacts);
        } else { // recursion
            WorkflowXmlSerializerVisitor serializer = new WorkflowXmlSerializerVisitor(subProcess);
            serializer.makeXml();
            addRootElements(serializer.getRootElements());

            // add as flow node to current scope
            addFlowNode(subProcess, serializer.getXmlSubProcess());
        }
    }

    @Override
    public void visit(final ASTLane lane) {
        TLane xmlLane = makeLaneAndAddToLaneSet(lane);
        laneStack.push(xmlLane);
    }

    @Override
    public void endVisit(final ASTLane lane) {
        laneStack.pop();
    }

    private TLane makeLaneAndAddToLaneSet(final ASTLane astLane) {
        if (null == xmlRootLaneSet) {
            xmlRootLaneSet = CommonFactory.makeXmlLaneSet();
        }

        final TLaneSet laneSet;
        if (laneStack.isEmpty()) {
            laneSet = xmlRootLaneSet;
        } else {
            TLane parentLane = laneStack.peek();
            if (null == parentLane.getChildLaneSet()) {
                TLaneSet childLaneSet = CommonFactory.makeXmlLaneSet();
                parentLane.setChildLaneSet(childLaneSet);
            }
            laneSet = parentLane.getChildLaneSet();
        }

        TLane lane = CommonFactory.makeXmlLane(astLane);
        laneSet.getLane().add(lane);

        return lane;
    }

    protected void handleSequenceFlow(final ASTFlowElementContainer container) {
        Map<SequenceFlow, TSequenceFlow> flows = WorkflowCollectors.toSequenceFlowLocal(container).stream()
                .collect(Collectors.toMap(Function.identity(), this::makeSequenceFlow));

        flows.keySet()
                .stream()
                .filter(SequenceFlow::isDefault)
                .forEach(defaultFlow -> { // add default flow reference. bit hacky
                    final TSequenceFlow tFlow = flows.get(defaultFlow);

                    defaultFlow.getSource().accept(new WorkflowInheritanceVisitor() {
                        @Override
                        public void visit(final ASTActivity activity) {
                            TActivity t = (TActivity) getXmlNode(activity);
                            t.setDefault(tFlow);
                        }
                        @Override
                        public void visit(final ASTGateway gateway) {
                            if (gateway.getType().isExclusive()) {
                                TExclusiveGateway t = (TExclusiveGateway) getXmlNode(gateway);
                                t.setDefault(tFlow);
                            }
                            if (gateway.getType().isInclusive()) {
                                TInclusiveGateway t = (TInclusiveGateway) getXmlNode(gateway);
                                t.setDefault(tFlow);
                            }
                            if (gateway.getType().isComplex()) {
                                TComplexGateway t = (TComplexGateway) getXmlNode(gateway);
                                t.setDefault(tFlow);
                            }
                        }
                    });
        });
    }

    protected TSequenceFlow makeSequenceFlow(final SequenceFlow sequenceFlow) {
        JAXBElement<TSequenceFlow> xmlNode = CommonFactory.makeXmlSequenceFlow(
                sequenceFlow, getXmlNode(sequenceFlow.getSource()), getXmlNode(sequenceFlow.getTarget()));
        addFlowElement(xmlNode);

        return xmlNode.getValue();
    }

    private void addSequenceFlowRefs(final ASTFlowNode astNode, final TFlowNode xmlNode) {
        // add references to sequence flow
        astNode.getIncomingList().forEach(flow -> xmlNode.getIncoming().add(new QName(WorkflowXmlUtils.getAsResourceKey(flow.getName()))));
        astNode.getOutgoingList().forEach(flow -> xmlNode.getOutgoing().add(new QName(WorkflowXmlUtils.getAsResourceKey(flow.getName()))));
    }

    private void addLaneFlowNodeRef(final TFlowNode xmlNode) {
        if (!laneStack.isEmpty()) {
            laneStack.peek().getFlowNodeRef().add(CommonFactory.makeXmlLaneFlowNodeRef(xmlNode));
        }
    }

    /**
     * Adds a flow node to the XML output
     * Also takes care about adding the fow node to the current lane (if any) and adding references to incoming/outgoing sequence flow.
     *
     * @param astNode
     * @param xmlNode
     */
    protected void addFlowNode(final ASTFlowNode astNode, final JAXBElement<? extends TFlowNode> xmlNode) {
        addFlowElement(xmlNode);
        flowNodeMap.put(astNode, xmlNode.getValue());

        addSequenceFlowRefs(astNode, xmlNode.getValue());
        addLaneFlowNodeRef(xmlNode.getValue());
    }

    /**
     * Adds a flow element to the XML output. For flow nodes, use {@link #addFlowNode(ASTFlowNode, JAXBElement)}.
     * @param xmlNode
     */
    protected void addFlowElement(final JAXBElement<? extends TFlowElement> xmlNode) {
        flowElements.add(xmlNode);
    }

    /**
     * Adds an artifact to the XML output.
     * @param xmlNode
     */
    protected void addArtifact(final JAXBElement<? extends TArtifact> xmlNode) {
        artifacts.add(xmlNode);
    }

    /**
     * Adds a root element to the XML output.
     * @param xmlNode
     */
    protected void addRootElement(final JAXBElement<? extends TRootElement> xmlNode) {
        rootElements.add(xmlNode);
    }

    /**
     * Adds all root elements to the XML output.
     * @param xmlNodes
     */
    protected void addRootElements(final Collection<JAXBElement<? extends TRootElement>> xmlNodes) {
        rootElements.addAll(xmlNodes);
    }

    protected TFlowNode getXmlNode(final ASTFlowNode flowNode) {
        return flowNodeMap.get(flowNode);
    }

    /**
     * @return XML process
     */
    public JAXBElement<TProcess> getXmlProcess() {
        return xmlProcess;
    }

    /**
     * @return XML sub-process
     */
    public JAXBElement<? extends TSubProcess> getXmlSubProcess() {
        return xmlSubProcess;
    }

    /**
     * @return All XML root elements. Does also include the process.
     */
    public List<JAXBElement<? extends TRootElement>> getRootElements() {
        return rootElements;
    }

}

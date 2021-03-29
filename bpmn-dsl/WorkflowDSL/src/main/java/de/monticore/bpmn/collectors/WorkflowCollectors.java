package de.monticore.bpmn.collectors;

import com.google.common.collect.Streams;
import de.monticore.bpmn.workflow._ast.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helpers to collect specific AST nodes.
 *
 * In general, it is recommended to implement the visitor interface instead of using collectors.
 *
 * @see WorkflowCollector
 * @see WorkflowLocalCollector
 */
public class WorkflowCollectors {

    public static List<ASTFlowElement> toFlowElements(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTFlowElement>(root) {
            @Override
            public void visit(ASTFlowElement node) {
                select(node);
            }
            // InlineFlowNode must be added separately, since InlineFlowNode is not visited as ASTFlowElement
            // (InlineFlowNode does only astextend, but not implement ASTInlineFlowNode)
            @Override
            public void visit(ASTInlineFlowNode node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTFlowElement> toFlowElementsLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTFlowElement>(root) {
            @Override
            public void visit(ASTFlowElement node) {
                select(node);
            }
            // InlineFlowNode must be added separately, since InlineFlowNode is not visited as ASTFlowElement
            // (InlineFlowNode does only astextend, but not implement ASTInlineFlowNode)
            @Override
            public void visit(ASTInlineFlowNode node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTFlowNode> toFlowNodes(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTFlowNode>(root) {
            @Override
            public void visit(ASTFlowNode node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTFlowNode> toFlowNodesLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTFlowNode>(root) {
            @Override
            public void visit(ASTFlowNode node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTActivity> toActivities(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTActivity>(root) {
            @Override
            public void visit(ASTActivity node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTActivity> toActivitiesLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTActivity>(root) {
            @Override
            public void visit(ASTActivity node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTSubProcess> toSubProcesses(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTSubProcess>(root) {
            @Override
            public void visit(ASTSubProcess node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTSubProcess> toSubProcessesLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTSubProcess>(root) {
            @Override
            public void visit(ASTSubProcess node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTCallActivity> toCallActivities(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTCallActivity>(root) {
            @Override
            public void visit(ASTCallActivity node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTCallActivity> toCallActivitiesLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTCallActivity>(root) {
            @Override
            public void visit(ASTCallActivity node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTTask> toTasks(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTTask>(root) {
            @Override
            public void visit(ASTTask node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTTask> toTasksLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTTask>(root) {
            @Override
            public void visit(ASTTask node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTGateway> toGateways(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTGateway>(root) {
            @Override
            public void visit(ASTGateway node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTGateway> toGatewaysLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTGateway>(root) {
            @Override
            public void visit(ASTGateway node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTEvent> toEvents(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTEvent>(root) {
            @Override
            public void visit(ASTEvent node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTEvent> toEventsLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTEvent>(root) {
            @Override
            public void traverse(ASTCallActivity callActivity) {
                // avoid selecting boundary events
            }
            @Override
            public void traverse(ASTTask task) {
                // avoid selecting boundary events
            }
            @Override
            public void visit(ASTEvent node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTDataObject> toDataObjects(final ASTFlowElementContainer root) {
        return new WorkflowCollector<ASTDataObject>(root) {
            @Override
            public void visit(ASTDataObject node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTDataObject> toDataObjectsLocal(final ASTFlowElementContainer root) {
        return new WorkflowLocalCollector<ASTDataObject>(root) {
            @Override
            public void visit(ASTDataObject node) {
                select(node);
            }
        }.collect();
    }

    public static List<ASTTask> toUserTasks(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toTasks(root)
                .stream()
                .filter(task -> task.getTypeOpt().map(ASTTaskType.USER::equals).orElse(false))
                .collect(Collectors.toList());
    }

    public static List<ASTTask> toServiceTasks(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toTasks(root)
                .stream()
                .filter(task -> task.getTypeOpt().map(ASTTaskType.SERVICE::equals).orElse(false))
                .collect(Collectors.toList());
    }

    public static List<ASTFlowNode> toStartNodesLocal(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toFlowNodesLocal(root)
                .stream()
                .filter(ASTFlowNode::isEmptyIncomings)
                .collect(Collectors.toList());
    }

    public static List<ASTFlowNode> toEndNodesLocal(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toFlowNodesLocal(root)
                .stream()
                .filter(ASTFlowNode::isEmptyOutgoings)
                .collect(Collectors.toList());
    }

    public static List<ASTEvent> toStartEventsLocal(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toEventsLocal(root).stream().filter(ASTEvent::isStart).collect(Collectors.toList());
    }

    public static List<ASTEvent> toEndEventsLocal(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toEventsLocal(root).stream().filter(ASTEvent::isEnd).collect(Collectors.toList());
    }

    public static List<SequenceFlow> toSequenceFlowLocal(final ASTFlowElementContainer root) {
        List<ASTFlowNode> flowNodes = toFlowNodesLocal(root);

        return Streams.concat(
                flowNodes.stream().map(ASTFlowNode::getIncomingList).flatMap(List::stream),
                flowNodes.stream().map(ASTFlowNode::getOutgoingList).flatMap(List::stream)
        )
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<SequenceFlow> toSequenceFlow(final ASTFlowElementContainer root) {
        List<ASTFlowNode> flowNodes = toFlowNodes(root);

        return Streams.concat(
                flowNodes.stream().map(ASTFlowNode::getIncomingList).flatMap(List::stream),
                flowNodes.stream().map(ASTFlowNode::getOutgoingList).flatMap(List::stream)
        )
                .distinct()
                .collect(Collectors.toList());
    }

}

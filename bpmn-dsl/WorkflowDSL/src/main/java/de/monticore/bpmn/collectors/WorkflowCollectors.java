package de.monticore.bpmn.collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import de.monticore.ast.ASTNode;
import de.monticore.bpmn.workflow.WorkflowMill;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.bpmn.workflow._visitor.WorkflowHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowInheritanceHandler;
import de.monticore.bpmn.workflow._visitor.WorkflowTraverser;

import java.util.List;
import java.util.Stack;
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

        WorkflowCollector<ASTFlowElement> collector = new WorkflowCollector<ASTFlowElement>(root) {
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
        };
        return collector.collect(collector);
    }

    public static List<ASTFlowElement> toFlowElementsLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTFlowElement> collector = new WorkflowLocalCollector<ASTFlowElement>(root) {
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
        };
        return collector.collect(collector);
    }

    public static List<ASTFlowNode> toFlowNodes(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTFlowNode> collector = new WorkflowCollector<ASTFlowNode>(root) {
            @Override
            public void visit(ASTFlowNode node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTFlowNode> toFlowNodesLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTFlowNode> collector = new WorkflowLocalCollector<ASTFlowNode>(root) {
            @Override
            public void visit(ASTFlowNode node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTActivity> toActivities(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTActivity> collector = new WorkflowCollector<ASTActivity>(root) {
            @Override
            public void visit(ASTActivity node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTActivity> toActivitiesLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTActivity> collector = new WorkflowLocalCollector<ASTActivity>(root) {
            @Override
            public void visit(ASTActivity node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTSubProcess> toSubProcesses(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTSubProcess> collector = new WorkflowCollector<ASTSubProcess>(root) {
            @Override
            public void visit(ASTSubProcess node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTSubProcess> toSubProcessesLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTSubProcess> collector = new WorkflowLocalCollector<ASTSubProcess>(root) {
            @Override
            public void visit(ASTSubProcess node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTCallActivity> toCallActivities(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTCallActivity> collector = new WorkflowCollector<ASTCallActivity>(root) {
            @Override
            public void visit(ASTCallActivity node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTCallActivity> toCallActivitiesLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTCallActivity> collector = new WorkflowLocalCollector<ASTCallActivity>(root) {
            @Override
            public void visit(ASTCallActivity node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTTask> toTasks(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTTask> collector = new WorkflowCollector<ASTTask>(root) {
            @Override
            public void visit(ASTTask node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTTask> toTasksLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTTask> collector = new WorkflowLocalCollector<ASTTask>(root) {
            @Override
            public void visit(ASTTask node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTGateway> toGateways(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTGateway> collector = new WorkflowCollector<ASTGateway>(root) {
            @Override
            public void visit(ASTGateway node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTGateway> toGatewaysLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTGateway> collector = new WorkflowLocalCollector<ASTGateway>(root) {
            @Override
            public void visit(ASTGateway node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTEvent> toEvents(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTEvent> collector = new WorkflowCollector<ASTEvent>(root) {
            @Override
            public void visit(ASTEvent node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTEvent> toEventsLocal(final ASTFlowElementContainer root) {
        return root.getFlowElementList().stream()
          .filter(elem -> elem instanceof ASTEvent)
          .map(elem -> (ASTEvent) elem)
          .collect(Collectors.toList());
    }

    public static List<ASTDataObject> toDataObjects(final ASTFlowElementContainer root) {
        WorkflowCollector<ASTDataObject> collector = new WorkflowCollector<ASTDataObject>(root) {
            @Override
            public void visit(ASTDataObject node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTDataObject> toDataObjectsLocal(final ASTFlowElementContainer root) {
        WorkflowLocalCollector<ASTDataObject> collector = new WorkflowLocalCollector<ASTDataObject>(root) {
            @Override
            public void visit(ASTDataObject node) {
                select(node);
            }
        };
        return collector.collect(collector);
    }

    public static List<ASTTask> toUserTasks(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toTasks(root)
                .stream()
                .filter(task -> task.isPresentType() && task.getType().equals(ASTTaskType.USER))
                .collect(Collectors.toList());
    }

    public static List<ASTTask> toServiceTasks(final ASTFlowElementContainer root) {
        return WorkflowCollectors.toTasks(root)
                .stream()
                .filter(task -> task.isPresentType() && task.getType().equals(ASTTaskType.SERVICE))
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
                flowNodes.stream().map(ASTFlowNode::getIncomingsList).flatMap(List::stream),
                flowNodes.stream().map(ASTFlowNode::getOutgoingsList).flatMap(List::stream)
        )
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<SequenceFlow> toSequenceFlow(final ASTFlowElementContainer root) {
        List<ASTFlowNode> flowNodes = toFlowNodes(root);

        return Streams.concat(
                flowNodes.stream().map(ASTFlowNode::getIncomingsList).flatMap(List::stream),
                flowNodes.stream().map(ASTFlowNode::getOutgoingsList).flatMap(List::stream)
        )
                .distinct()
                .collect(Collectors.toList());
    }



}

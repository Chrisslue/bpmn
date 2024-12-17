package de.monticore.bpmn.collectors;

import com.google.common.collect.Streams;
import de.monticore.bpmn.workflow._ast.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helpers to collect specific AST nodes.
 *
 * <p>In general, it is recommended to implement the visitor interface instead of using collectors.
 *
 * @see WorkflowCollector
 * @see WorkflowLocalCollector
 */
// functions will not work properly as we decided to drop the terminal ASTFlowElementContainer
public class WorkflowCollectors {

  public static List<ASTFlowElement> toFlowElements(final ASTProcess root) {

    WorkflowCollector<ASTFlowElement> collector =
        new WorkflowCollector<ASTFlowElement>(root) {
          @Override
          public void visit(ASTFlowElement node) {
            select(node);
          }
          // InlineFlowNode must be added separately, since InlineFlowNode is not visited as
          // ASTFlowElement
          // (InlineFlowNode does only astextend, but not implement ASTInlineFlowNode)
          //@Override
          //public void visit(ASTFlowElement node) {
          //  select(node);
          //}
        };
    return collector.collect(collector);
  }

  public static List<ASTFlowElement> toFlowElementsLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTFlowElement> collector =
        new WorkflowLocalCollector<ASTFlowElement>(root) {
          @Override
          public void visit(ASTFlowElement node) {
            select(node);
          }
          // InlineFlowNode must be added separately, since InlineFlowNode is not visited as
          // ASTFlowElement
          // (InlineFlowNode does only astextend, but not implement ASTInlineFlowNode)
          //@Override
          //public void visit(ASTFlowElement node) {
          //  select(node);
          //}
        };
    return collector.collect(collector);
  }

  public static List<ASTFlowElement> toFlowNodes(final ASTProcess root) {
    WorkflowCollector<ASTFlowElement> collector =
        new WorkflowCollector<ASTFlowElement>(root) {
          @Override
          public void visit(ASTFlowElement node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTFlowElement> toFlowNodesLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTFlowElement> collector =
        new WorkflowLocalCollector<ASTFlowElement>(root) {
          @Override
          public void visit(ASTFlowElement node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTActivity> toActivities(final ASTProcess root) {
    WorkflowCollector<ASTActivity> collector =
        new WorkflowCollector<ASTActivity>(root) {
          @Override
          public void visit(ASTActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTActivity> toActivitiesLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTActivity> collector =
        new WorkflowLocalCollector<ASTActivity>(root) {
          @Override
          public void visit(ASTActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }
  
  public static List<ASTActivity> toActivitiesLocalSubProcess(final ASTSubProcess root) {
    WorkflowLocalCollector<ASTActivity> collector =
        new WorkflowLocalCollector<ASTActivity>(root) {
          @Override
          public void visit(ASTActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }
  
  public static List<ASTSubProcess> toSubProcesses(final ASTProcess root) {
    WorkflowCollector<ASTSubProcess> collector =
        new WorkflowCollector<ASTSubProcess>(root) {
          @Override
          public void visit(ASTSubProcess node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTSubProcess> toSubProcessesLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTSubProcess> collector =
        new WorkflowLocalCollector<ASTSubProcess>(root) {
          @Override
          public void visit(ASTSubProcess node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTCallActivity> toCallActivities(final ASTProcess root) {
    WorkflowCollector<ASTCallActivity> collector =
        new WorkflowCollector<ASTCallActivity>(root) {
          @Override
          public void visit(ASTCallActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTCallActivity> toCallActivitiesLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTCallActivity> collector =
        new WorkflowLocalCollector<ASTCallActivity>(root) {
          @Override
          public void visit(ASTCallActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTTask> toTasks(final ASTProcess root) {
    WorkflowCollector<ASTTask> collector =
        new WorkflowCollector<ASTTask>(root) {
          @Override
          public void visit(ASTTask node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTTask> toTasksLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTTask> collector =
        new WorkflowLocalCollector<ASTTask>(root) {
          @Override
          public void visit(ASTTask node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTGateway> toGateways(final ASTProcess root) {
    WorkflowCollector<ASTGateway> collector =
        new WorkflowCollector<ASTGateway>(root) {
          @Override
          public void visit(ASTGateway node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTGateway> toGatewaysLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTGateway> collector =
        new WorkflowLocalCollector<ASTGateway>(root) {
          @Override
          public void visit(ASTGateway node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTEvent> toEvents(final ASTProcess root) {
    WorkflowCollector<ASTEvent> collector =
        new WorkflowCollector<ASTEvent>(root) {
          @Override
          public void visit(ASTEvent node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTEvent> toEventsLocal(final ASTProcess root) {
    return root.getFlowElementList().stream()
        .filter(elem -> elem instanceof ASTEvent)
        .map(elem -> (ASTEvent) elem)
        .collect(Collectors.toList());
  }

  public static List<ASTEvent> toEventsLocalSubProcess(final ASTSubProcess root) {
    return root.getFlowElementList().stream()
        .filter(elem -> elem instanceof ASTEvent)
        .map(elem -> (ASTEvent) elem)
        .collect(Collectors.toList());
  }

  public static List<ASTDataObject> toDataObjects(final ASTProcess root) {
    WorkflowCollector<ASTDataObject> collector =
        new WorkflowCollector<ASTDataObject>(root) {
          @Override
          public void visit(ASTDataObject node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTDataObject> toDataObjectsLocal(final ASTProcess root) {
    WorkflowLocalCollector<ASTDataObject> collector =
        new WorkflowLocalCollector<ASTDataObject>(root) {
          @Override
          public void visit(ASTDataObject node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTTask> toUserTasks(final ASTProcess root) {
    return WorkflowCollectors.toTasks(root).stream()
        .filter(task -> task.getType() == ASTConstantsWorkflow.USER)
        .collect(Collectors.toList());
  }

  public static List<ASTTask> toServiceTasks(final ASTProcess root) {
    return WorkflowCollectors.toTasks(root).stream()
        .filter(task -> task.getType() == ASTConstantsWorkflow.SERVICE)
        .collect(Collectors.toList());
  }

  public static List<ASTFlowElement> toStartNodesLocal(final ASTProcess root) {
    return WorkflowCollectors.toFlowNodesLocal(root).stream()
        .filter(ASTFlowElement::isEmptyIncomings)
        .collect(Collectors.toList());
  }

  public static List<ASTFlowElement> toEndNodesLocal(final ASTProcess root) {
    return WorkflowCollectors.toFlowNodesLocal(root).stream()
        .filter(ASTFlowElement::isEmptyOutgoings)
        .collect(Collectors.toList());
  }

  public static List<ASTEvent> toStartEventsLocal(final ASTProcess root) {
    return WorkflowCollectors.toEventsLocal(root).stream()
        .filter(ASTEvent::isStart)
        .collect(Collectors.toList());
  }

  public static List<ASTEvent> toStartEventsLocalSubProcess(final ASTSubProcess root) {
    return WorkflowCollectors.toEventsLocalSubProcess(root).stream()
        .filter(ASTEvent::isStart)
        .collect(Collectors.toList());
  }

  public static List<ASTEvent> toEndEventsLocalSubProcess(final ASTSubProcess root) {
    return WorkflowCollectors.toEventsLocalSubProcess(root).stream()
        .filter(ASTEvent::isEnd)
        .collect(Collectors.toList());
  }

  public static List<ASTEvent> toEndEventsLocal(final ASTProcess root) {
    return WorkflowCollectors.toEventsLocal(root).stream()
        .filter(ASTEvent::isEnd)
        .collect(Collectors.toList());
  }

  public static List<SequenceFlow> toSequenceFlowLocal(final ASTProcess root) {
    List<ASTFlowElement> flowNodes = toFlowNodesLocal(root);

    return Streams.concat(
            flowNodes.stream().map(ASTFlowElement::getIncomingsList).flatMap(List::stream),
            flowNodes.stream().map(ASTFlowElement::getOutgoingsList).flatMap(List::stream))
        .distinct()
        .collect(Collectors.toList());
  }

  public static List<SequenceFlow> toSequenceFlow(final ASTProcess root) {
    List<ASTFlowElement> flowNodes = toFlowNodes(root);

    return Streams.concat(
            flowNodes.stream().map(ASTFlowElement::getIncomingsList).flatMap(List::stream),
            flowNodes.stream().map(ASTFlowElement::getOutgoingsList).flatMap(List::stream))
        .distinct()
        .collect(Collectors.toList());
  }
}

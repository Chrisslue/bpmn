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

  public static List<ASTFlowElement> toFlowElements(final ASTWFProcess root) {

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

  public static List<ASTFlowElement> toFlowElementsLocal(final ASTWFProcess root) {
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

  public static List<ASTFlowElement> toFlowNodes(final ASTWFProcess root) {
    WorkflowCollector<ASTFlowElement> collector =
        new WorkflowCollector<ASTFlowElement>(root) {
          @Override
          public void visit(ASTFlowElement node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTFlowElement> toFlowNodesLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTFlowElement> collector =
        new WorkflowLocalCollector<ASTFlowElement>(root) {
          @Override
          public void visit(ASTFlowElement node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFActivity> toActivities(final ASTWFProcess root) {
    WorkflowCollector<ASTWFActivity> collector =
        new WorkflowCollector<ASTWFActivity>(root) {
          @Override
          public void visit(ASTWFActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFActivity> toActivitiesLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTWFActivity> collector =
        new WorkflowLocalCollector<ASTWFActivity>(root) {
          @Override
          public void visit(ASTWFActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }
  
  public static List<ASTWFActivity> toActivitiesLocalSubProcess(final ASTWFSubProcess root) {
    WorkflowLocalCollector<ASTWFActivity> collector =
        new WorkflowLocalCollector<ASTWFActivity>(root) {
          @Override
          public void visit(ASTWFActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }
  
  public static List<ASTWFSubProcess> toSubProcesses(final ASTWFProcess root) {
    WorkflowCollector<ASTWFSubProcess> collector =
        new WorkflowCollector<ASTWFSubProcess>(root) {
          @Override
          public void visit(ASTWFSubProcess node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFSubProcess> toSubProcessesLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTWFSubProcess> collector =
        new WorkflowLocalCollector<ASTWFSubProcess>(root) {
          @Override
          public void visit(ASTWFSubProcess node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFCallActivity> toCallActivities(final ASTWFProcess root) {
    WorkflowCollector<ASTWFCallActivity> collector =
        new WorkflowCollector<ASTWFCallActivity>(root) {
          @Override
          public void visit(ASTWFCallActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFCallActivity> toCallActivitiesLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTWFCallActivity> collector =
        new WorkflowLocalCollector<ASTWFCallActivity>(root) {
          @Override
          public void visit(ASTWFCallActivity node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFTask> toTasks(final ASTWFProcess root) {
    WorkflowCollector<ASTWFTask> collector =
        new WorkflowCollector<ASTWFTask>(root) {
          @Override
          public void visit(ASTWFTask node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFTask> toTasksLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTWFTask> collector =
        new WorkflowLocalCollector<ASTWFTask>(root) {
          @Override
          public void visit(ASTWFTask node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFGateway> toGateways(final ASTWFProcess root) {
    WorkflowCollector<ASTWFGateway> collector =
        new WorkflowCollector<ASTWFGateway>(root) {
          @Override
          public void visit(ASTWFGateway node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFGateway> toGatewaysLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTWFGateway> collector =
        new WorkflowLocalCollector<ASTWFGateway>(root) {
          @Override
          public void visit(ASTWFGateway node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFEvent> toEvents(final ASTWFProcess root) {
    WorkflowCollector<ASTWFEvent> collector =
        new WorkflowCollector<ASTWFEvent>(root) {
          @Override
          public void visit(ASTWFEvent node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFEvent> toEventsLocal(final ASTWFProcess root) {
    return root.getFlowElementList().stream()
        .filter(elem -> elem instanceof ASTWFEvent)
        .map(elem -> (ASTWFEvent) elem)
        .collect(Collectors.toList());
  }

  public static List<ASTWFEvent> toEventsLocalSubProcess(final ASTWFSubProcess root) {
    return root.getFlowElementList().stream()
        .filter(elem -> elem instanceof ASTWFEvent)
        .map(elem -> (ASTWFEvent) elem)
        .collect(Collectors.toList());
  }

  public static List<ASTWFDataObject> toDataObjects(final ASTWFProcess root) {
    WorkflowCollector<ASTWFDataObject> collector =
        new WorkflowCollector<ASTWFDataObject>(root) {
          @Override
          public void visit(ASTWFDataObject node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFDataObject> toDataObjectsLocal(final ASTWFProcess root) {
    WorkflowLocalCollector<ASTWFDataObject> collector =
        new WorkflowLocalCollector<ASTWFDataObject>(root) {
          @Override
          public void visit(ASTWFDataObject node) {
            select(node);
          }
        };
    return collector.collect(collector);
  }

  public static List<ASTWFTask> toUserTasks(final ASTWFProcess root) {
    return WorkflowCollectors.toTasks(root).stream()
        .filter(task -> task.getType() == ASTConstantsWorkflow.USER)
        .collect(Collectors.toList());
  }

  public static List<ASTWFTask> toServiceTasks(final ASTWFProcess root) {
    return WorkflowCollectors.toTasks(root).stream()
        .filter(task -> task.getType() == ASTConstantsWorkflow.SERVICE)
        .collect(Collectors.toList());
  }

  public static List<ASTFlowElement> toStartNodesLocal(final ASTWFProcess root) {
    return WorkflowCollectors.toFlowNodesLocal(root).stream()
        .filter(ASTFlowElement::isEmptyIncomings)
        .collect(Collectors.toList());
  }

  public static List<ASTFlowElement> toEndNodesLocal(final ASTWFProcess root) {
    return WorkflowCollectors.toFlowNodesLocal(root).stream()
        .filter(ASTFlowElement::isEmptyOutgoings)
        .collect(Collectors.toList());
  }

  public static List<ASTWFEvent> toStartEventsLocal(final ASTWFProcess root) {
    return WorkflowCollectors.toEventsLocal(root).stream()
        .filter(ASTWFEvent::isStart)
        .collect(Collectors.toList());
  }

  public static List<ASTWFEvent> toStartEventsLocalSubProcess(final ASTWFSubProcess root) {
    return WorkflowCollectors.toEventsLocalSubProcess(root).stream()
        .filter(ASTWFEvent::isStart)
        .collect(Collectors.toList());
  }

  public static List<ASTWFEvent> toEndEventsLocalSubProcess(final ASTWFSubProcess root) {
    return WorkflowCollectors.toEventsLocalSubProcess(root).stream()
        .filter(ASTWFEvent::isEnd)
        .collect(Collectors.toList());
  }

  public static List<ASTWFEvent> toEndEventsLocal(final ASTWFProcess root) {
    return WorkflowCollectors.toEventsLocal(root).stream()
        .filter(ASTWFEvent::isEnd)
        .collect(Collectors.toList());
  }

  public static List<SequenceFlow> toSequenceFlowLocal(final ASTWFProcess root) {
    List<ASTFlowElement> flowNodes = toFlowNodesLocal(root);

    return Streams.concat(
            flowNodes.stream().map(ASTFlowElement::getIncomingsList).flatMap(List::stream),
            flowNodes.stream().map(ASTFlowElement::getOutgoingsList).flatMap(List::stream))
        .distinct()
        .collect(Collectors.toList());
  }

  public static List<SequenceFlow> toSequenceFlow(final ASTWFProcess root) {
    List<ASTFlowElement> flowNodes = toFlowNodes(root);

    return Streams.concat(
            flowNodes.stream().map(ASTFlowElement::getIncomingsList).flatMap(List::stream),
            flowNodes.stream().map(ASTFlowElement::getOutgoingsList).flatMap(List::stream))
        .distinct()
        .collect(Collectors.toList());
  }
}

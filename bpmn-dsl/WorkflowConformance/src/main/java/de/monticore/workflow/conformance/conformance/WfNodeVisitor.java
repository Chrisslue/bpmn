package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import java.util.List;

public interface WfNodeVisitor {

  boolean accept(WfNode node, List<WfNode> branchId);
}

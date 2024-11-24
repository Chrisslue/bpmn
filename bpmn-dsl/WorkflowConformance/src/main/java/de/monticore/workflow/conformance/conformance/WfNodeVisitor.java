package de.monticore.workflow.conformance.conformance;

import de.monticore.workflow.conformance.datastructure.interf.WfNode;
import de.monticore.workflow.conformance.utils.BranchID;

public interface WfNodeVisitor {

  boolean accept(WfNode node, BranchID branchId);
}

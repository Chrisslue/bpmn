package de.monticore.bpmn.conformance.datastructures.interf;

import de.monticore.bpmn.conformance.datastructures.utils.BranchID;

public interface WfNodeVisitor {

  boolean accept(WfNode node, BranchID branchId);
}

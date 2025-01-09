package de.monticore.bpmn.conformance.datastructures.interf;

import de.monticore.bpmn.conformance.datastructures.utils.BranchID;
import java.util.Set;

public interface WfNodeVisitor {

  boolean accept(Set<BranchID> branchIDS);
}

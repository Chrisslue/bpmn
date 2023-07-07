package de.monticore.wf2ma;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcFieldDeclaration;
import de.monticore.lts.LTSBuilder;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import java.util.Set;
import montiarc._ast.ASTMACompilationUnit;

public interface ArcSCBuilder extends LTSBuilder<ASTSCState, ASTTransitionAction> {
  ASTArcStatechart buildSC();

  ASTMACompilationUnit buildMA(String name);

  Set<ASTSCState> getStates();

  Set<ASTSCTransition> getTransitions();

  Set<ASTArcFieldDeclaration> getVariables();

  String getOutPortName();
}

package de.monticore.bpmn.wf2ma;

import arcautomaton._ast.ASTArcStatechart;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import montiarc.MontiArcMill;
import montiarc.MontiArcTool;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcCoCos;
import montiarc.check.MontiArcTypeCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ArcSCBuilderTest {

  @BeforeEach
  void init() {
    MontiArcMill.init();
    MontiArcMill.globalScope().init();
    MontiArcTypeCheck.init();
    new MontiArcTool().initializeClass2MC();
  }

  @Test
  void testBuildStringSC() {
    StringArcSCBuilder builder = new StringArcSCBuilder("out");
    ASTSCState state1 = builder.addInitialState("Anon");
    ASTSCState state2 = builder.addState("Known");
    ASTTransitionAction label12 = builder.addLabel("LOGIN_SUCCESSFUL");
    ASTTransitionAction label21 = builder.addLabel("LOG_OUT");
    builder.addTransition(state1, state2, label12);
    builder.addTransition(state2, state1, label21);
    ASTArcStatechart sc = builder.buildSC();
    System.out.println(MontiArcMill.prettyPrint(sc, false));
  }

  @Test
  void testBuildStringMA() {
    StringArcSCBuilder builder = new StringArcSCBuilder("out");
    ASTSCState state1 = builder.addInitialState("Anon");
    ASTSCState state2 = builder.addState("Known");
    ASTTransitionAction label12 = builder.addLabel("LOGIN_SUCCESSFUL");
    ASTTransitionAction label21 = builder.addLabel("LOG_OUT");
    builder.addTransition(state1, state2, label12);
    builder.addTransition(state2, state1, label21);
    ASTMACompilationUnit ma = builder.buildMA("Test");
    MontiArcMill.scopesGenitorDelegator().createFromAST(ma);
    MontiArcMill.scopesGenitorP2Delegator().createFromAST(ma);
    MontiArcMill.scopesGenitorP3Delegator().createFromAST(ma);
    MontiArcCoCos.afterParser().checkAll(ma);
    // todo: ArcSCBuilder needs to be fixed
    // MontiArcCoCos.afterSymTab().checkAll(ma);
    System.out.println(MontiArcMill.prettyPrint(ma, false));
  }
}

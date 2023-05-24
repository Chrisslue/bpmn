package de.monticore.wf2ma;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcFieldDeclaration;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.ltl.LTSBuilder;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;

public class MASCBuilder implements LTSBuilder<ASTSCState, ASTTransitionAction> {

  Set<ASTSCState> states;
  Set<ASTSCTransition> transitions;
  Set<ASTArcFieldDeclaration> variables;
  String out;

  public MASCBuilder(String outPortName) {
    out = outPortName;
  }

  @Override
  public void addVariable(String varName, ASTMCQualifiedType varType, ASTExpression value) {
    variables.add(
        MontiArcMill.arcFieldDeclarationBuilder()
            .setMCType(varType)
            .addArcField(varName, value)
            .build());
  }

  @Override
  public ASTTransitionAction addLabel(String label) {
    try {
      Optional<ASTMCBlockStatement> statement =
          MontiArcMill.parser().parse_StringMCBlockStatement(out + " = " + label + ";");
      assert statement.isPresent();
      return MontiArcMill.transitionActionBuilder().setMCBlockStatement(statement.get()).build();
    } catch (IOException e) {
      Log.error(e.getMessage());
    }
    return null;
  }

  @Override
  public ASTSCState addState(String name) {
    ASTSCState state =
        MontiArcMill.sCStateBuilder()
            .setName(name)
            .setSCModifier(
                MontiArcMill.sCModifierBuilder().setInitial(false).setFinal(false).build())
            .setSCSAnte(MontiArcMill.sCEmptyAnteBuilder().build())
            .setSCSBody(MontiArcMill.sCEmptyBodyBuilder().build())
            .build();
    states.add(state);
    return state;
  }

  @Override
  public ASTSCState addInitialState(String name) {
    ASTSCState state =
        MontiArcMill.sCStateBuilder()
            .setName(name)
            .setSCModifier(
                MontiArcMill.sCModifierBuilder().setInitial(true).setFinal(false).build())
            .setSCSAnte(MontiArcMill.sCEmptyAnteBuilder().build())
            .setSCSBody(MontiArcMill.sCEmptyBodyBuilder().build())
            .build();
    states.add(state);
    return state;
  }

  @Override
  public ASTSCState addFinalState(String name) {
    ASTSCState state =
        MontiArcMill.sCStateBuilder()
            .setName(name)
            .setSCModifier(
                MontiArcMill.sCModifierBuilder().setInitial(false).setFinal(true).build())
            .setSCSAnte(MontiArcMill.sCEmptyAnteBuilder().build())
            .setSCSBody(MontiArcMill.sCEmptyBodyBuilder().build())
            .build();
    states.add(state);
    return state;
  }

  @Override
  public void addTransition(
      ASTSCState source, ASTSCState target, ASTTransitionAction label, ASTExpression condition) {
    transitions.add(
        MontiArcMill.sCTransitionBuilder()
            .setSourceName(source.getName())
            .setTargetName(target.getName())
            .setSCTBody(
                MontiArcMill.transitionBodyBuilder()
                    .setPre(condition)
                    .setTransitionAction(label)
                    .build())
            .build());
  }

  public ASTArcStatechart buildSC() {
    return MontiArcMill.arcStatechartBuilder()
        .addAllSCStatechartElements(states)
        .addAllSCStatechartElements(transitions)
        .build();
  }

  public ASTMACompilationUnit buildMA(String name) {
    return MontiArcMill.mACompilationUnitBuilder()
        .setComponentType(
            MontiArcMill.componentTypeBuilder()
                .setName(name)
                .setHead(MontiArcMill.componentHeadBuilder().build())
                .setBody(
                    MontiArcMill.componentBodyBuilder()
                        .addAllArcElements(variables)
                        .addArcElement(buildSC())
                        .build())
                .build())
        .build();
  }

  public Set<ASTSCState> getStates() {
    return states;
  }

  public Set<ASTSCTransition> getTransitions() {
    return transitions;
  }

  public Set<ASTArcFieldDeclaration> getVariables() {
    return variables;
  }

  public String getOutPortName() {
    return out;
  }
}

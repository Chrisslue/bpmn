package de.monticore.bpmn.wf2ma;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import de.monticore.cd.facade.MCQualifiedNameFacade;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.scbasis._ast.ASTSCState;
import de.monticore.scbasis._ast.ASTSCTransition;
import de.monticore.sctransitions4code._ast.ASTTransitionAction;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.logging.Log;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import montiarc.MontiArcMill;
import montiarc._ast.ASTMACompilationUnit;

public class StringArcSCBuilder implements ArcSCBuilder {

  protected Set<ASTSCState> states = new HashSet<>();
  protected Set<ASTSCTransition> transitions = new HashSet<>();
  protected Set<ASTArcFieldDeclaration> variables = new HashSet<>();
  protected String out;

  public StringArcSCBuilder(String outPortName) {
    char[] outName = outPortName.toCharArray();
    outName[0] = Character.toUpperCase(outName[0]);
    out = new String(outName);
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
          MontiArcMill.parser().parse_StringMCBlockStatement(out + " = \"" + label + "\";");
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
  public void addTransition(ASTSCState source, ASTSCState target, ASTTransitionAction label) {
    transitions.add(
        MontiArcMill.sCTransitionBuilder()
            .setSourceName(source.getName())
            .setTargetName(target.getName())
            .setSCTBody(
                MontiArcMill.transitionBodyBuilder()
                    .setPreAbsent()
                    .setTransitionAction(label)
                    .build())
            .build());
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

  // todo: add dummy events to transitions
  public ASTMACompilationUnit buildMA(String name) {

    ASTPortDeclaration outPort =
        MontiArcMill.portDeclarationBuilder()
            .addPort(out)
            .setMCType(MCTypeFacade.getInstance().createStringType())
            .setPortDirection(MontiArcMill.portDirectionBuilder().setOut(true).setIn(false).build())
            .build();

    ASTComponentInterface compInterface =
        MontiArcMill.componentInterfaceBuilder().setPortDeclarationsList(List.of(outPort)).build();

    return MontiArcMill.mACompilationUnitBuilder()
        .setImportStatementList(
            List.of(
                MontiArcMill.mCImportStatementBuilder()
                    .setMCQualifiedName(
                        MCQualifiedNameFacade.createQualifiedName("java.lang.String"))
                    .build()))
        .setComponentType(
            MontiArcMill.componentTypeBuilder()
                .setName(name)
                .setHead(MontiArcMill.componentHeadBuilder().build())
                .setBody(
                    MontiArcMill.componentBodyBuilder()
                        .addAllArcElements(variables)
                        .addArcElement(buildSC())
                        .addArcElement(compInterface)
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

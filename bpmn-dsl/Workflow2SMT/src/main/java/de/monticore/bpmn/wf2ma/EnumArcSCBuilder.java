package de.monticore.bpmn.wf2ma;

import arcautomaton._ast.ASTArcStatechart;
import arcbasis._ast.ASTArcFieldDeclaration;
import arcbasis._ast.ASTArcParameter;
import arcbasis._ast.ASTComponentInterface;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._symboltable.SymbolService;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
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

// todo: fix me
public class EnumArcSCBuilder implements ArcSCBuilder {

  protected Set<ASTSCState> states = new HashSet<>();
  protected Set<ASTSCTransition> transitions = new HashSet<>();
  protected Set<ASTArcFieldDeclaration> variables = new HashSet<>();
  protected String out;
  protected ASTCDEnum outType;

  public EnumArcSCBuilder(String outPortName) {
    char[] outName = outPortName.toCharArray();
    outName[0] = Character.toUpperCase(outName[0]);
    outType =
        CD4CodeMill.cDEnumBuilder()
            .setName(new String(outName))
            .setModifier(CD4CodeMill.modifierBuilder().build())
            .build();
    outName[0] = Character.toLowerCase(outName[0]);
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
    initLabel(label);
    try {
      Optional<ASTMCBlockStatement> statement =
          MontiArcMill.parser()
              .parse_StringMCBlockStatement(out + " = " + outType.getName() + "." + label + ";");
      assert statement.isPresent();
      return MontiArcMill.transitionActionBuilder().setMCBlockStatement(statement.get()).build();
    } catch (IOException e) {
      Log.error(e.getMessage());
    }
    return null;
  }

  protected void initLabel(String label) {
    if (outType.getCDEnumConstantList().stream().noneMatch(con -> con.getName().equals(label))) {
      outType.addCDEnumConstant(
          CD4CodeMill.cD4CodeEnumConstantBuilder().setName(label).setArgumentsAbsent().build());
    }
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

    ASTCDCompilationUnit cd =
        CD4CodeMill.cDCompilationUnitBuilder()
            .setCDDefinition(
                CD4CodeMill.cDDefinitionBuilder()
                    .setName(name + "CD")
                    .setCDElementsList(List.of(outType))
                    .set_SourcePositionStartAbsent()
                    .set_SourcePositionEndAbsent()
                    .setModifier(CD4CodeMill.modifierBuilder().build())
                    .build())
            .set_SourcePositionStartAbsent()
            .set_SourcePositionEndAbsent()
            .setMCPackageDeclarationAbsent()
            .build();

    CD4CodeMill.scopesGenitorDelegator().createFromAST(cd);
    cd.accept(new CD4CodeSymbolTableCompleter(cd).getTraverser());
    CD4CodeMill.globalScope().removeSubScope(cd.getEnclosingScope());
    outType.getSymbol().setSpannedScope(MontiArcMill.scope());
    SymbolService.link(MontiArcMill.globalScope(), outType.getSymbol());

    ASTPortDeclaration outPort =
        MontiArcMill.portDeclarationBuilder()
            .addPort(out)
            .setMCType(MCTypeFacade.getInstance().createQualifiedType(outType.getName()))
            .setPortDirection(MontiArcMill.portDirectionBuilder().setOut(true).setIn(false).build())
            .build();

    ASTComponentInterface compInterface =
        MontiArcMill.componentInterfaceBuilder().setPortDeclarationsList(List.of(outPort)).build();

    ASTArcParameter param =
        MontiArcMill.arcParameterBuilder()
            .setName(out)
            .setMCType(MCTypeFacade.getInstance().createQualifiedType(outType.getName()))
            .build();

    return MontiArcMill.mACompilationUnitBuilder()
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

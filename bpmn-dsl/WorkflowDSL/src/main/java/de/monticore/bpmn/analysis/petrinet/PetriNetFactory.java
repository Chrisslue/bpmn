 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn.analysis.petrinet;

import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import petrinet.PetrinetMill;
import petrinet._ast.*;

/** Factory for Petri net AST nodes. */
public class PetriNetFactory {

  public static ASTPetrinet createEmptyPetriNet(final String name) {
    return PetrinetMill.petrinetBuilder().setName(name).build();
  }

  public static ASTPlace createPlace(final String name) {
    return PetrinetMill.placeBuilder().setName(name).build();
  }

  public static ASTTransition createTransition(final String name) {
    return PetrinetMill.transitionBuilder().setName(name).build();
  }

  public static ASTFromEdge createEdgeFrom(final String fromPlace) {

    return PetrinetMill.fromEdgeBuilder().setPlace(fromPlace).setCount(getNatLiteral(1)).build();
  }

  public static ASTToEdge createEdgeTo(final String toPlace) {
    return PetrinetMill.toEdgeBuilder().setPlace(toPlace).setCount(getNatLiteral(1)).build();
  }

  private static ASTNatLiteral getNatLiteral(final int count) {
    return MCCommonLiteralsMill.natLiteralBuilder().setDigits(String.valueOf(count)).build();
  }
}

package de.monticore.bpmn.analysis.petrinet;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.LiteralsMill;
import petrinet._ast.*;

/**
 * Factory for Petri net AST nodes.
 */
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

        return PetrinetMill.fromEdgeBuilder().setPlace(fromPlace).setCount(getIntLiteral(1)).build();
    }

    public static ASTToEdge createEdgeTo(final String toPlace) {
        return PetrinetMill.toEdgeBuilder().setPlace(toPlace).setCount(getIntLiteral(1)).build();
    }

    private static ASTIntLiteral getIntLiteral(final int count) {
        return LiteralsMill.intLiteralBuilder().setSource(String.valueOf(count)).build();
    }

}

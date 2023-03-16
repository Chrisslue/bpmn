package de.monticore.bpmn.analysis.petrinet;

import petrinet.PetrinetMill;
import petrinet._ast.*;
import petrinet._symboltable.PetrinetScopesGenitorDelegator;

/**
 * Utilities for building Petri nets.
 */
public class PetriNetUtils {

    public static void buildSymTab(final ASTPetrinet petrinet) {
        PetrinetScopesGenitorDelegator scopesGenitor = PetrinetMill.scopesGenitorDelegator();
        scopesGenitor.createFromAST(petrinet);
    }

    public static void connect(final ASTPlace place, final ASTTransition transition) {
        final ASTFromEdge edge = PetriNetFactory.createEdgeFrom(place.getName());
        transition.addFromEdge(edge);
    }

    public static void connect(final ASTTransition transition, final ASTPlace place) {
        final ASTToEdge edge = PetriNetFactory.createEdgeTo(place.getName());
        transition.addToEdge(edge);
    }

    public static ASTTransition connect(final ASTPlace source, final ASTPlace target, final String transitionName) {
        final ASTTransition transition = PetriNetFactory.createTransition(transitionName);
        connect(source, transition);
        connect(transition, target);

        return transition;
    }

}

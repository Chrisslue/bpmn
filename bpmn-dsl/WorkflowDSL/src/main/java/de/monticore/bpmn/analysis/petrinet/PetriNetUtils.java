package de.monticore.bpmn.analysis.petrinet;

import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import petrinet._ast.*;
import petrinet._symboltable.PetrinetLanguage;
import petrinet._symboltable.PetrinetSymbolTableCreator;

import java.util.Optional;

/**
 * Utilities for building Petri nets.
 */
public class PetriNetUtils {

    public static void buildSymTab(final ASTPetrinet petrinet) {
        final PetrinetLanguage lang = new PetrinetLanguage();

        final ResolvingConfiguration resolverConfig = new ResolvingConfiguration();
        resolverConfig.addDefaultFilters(lang.getResolvingFilters());

        final GlobalScope globalScope = new GlobalScope(new ModelPath(), lang, resolverConfig);

        final Optional<PetrinetSymbolTableCreator> symbolTable = lang
                .getSymbolTableCreator(resolverConfig, globalScope);

        symbolTable.get().createFromAST(petrinet);
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

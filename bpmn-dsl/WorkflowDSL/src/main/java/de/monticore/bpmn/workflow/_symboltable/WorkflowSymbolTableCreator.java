/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.bpmn.Messages;
import de.monticore.bpmn.workflow._ast.*;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.types.references.ActualTypeArgument;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTImportStatement;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.symboltable.CDTypes;
import de.monticore.umlcd4a.symboltable.references.CDTypeSymbolReference;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import ocl.monticoreocl.ocl._symboltable.OCLVariableDeclarationSymbol;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WorkflowSymbolTableCreator extends WorkflowSymbolTableCreatorTOP {

    public WorkflowSymbolTableCreator(final ResolvingConfiguration resolverConfig, final MutableScope enclosingScope) {
        super(resolverConfig, enclosingScope);
    }

    @Override
    public void visit(final ASTWorkflowCompilationUnit compilationUnit) {
        Log.debug("Building symtab for " + compilationUnit.getProcess().getName(),
                WorkflowSymbolTableCreator.class.getSimpleName());

        super.visit(compilationUnit);

        final ArtifactScope artifactScope = createArtifactScope(compilationUnit);
        putOnStack(artifactScope);
    }

    @Override
    public void endVisit(final ASTWorkflowCompilationUnit compilationUnit) {
        super.endVisit(compilationUnit);

        Log.debug("Finished building symtab for " + compilationUnit.getProcess().getName(),
                WorkflowSymbolTableCreator.class.getSimpleName());
    }

    @Override
    public void visit(final ASTProcess process) {
        ProcessSymbol processSymbol = new ProcessSymbolBuilder()
                //.ioSpecification(process.getIOSpecification())
                .name(process.getName())
                .build();
        process.setProcessSymbol(processSymbol);

        addToScopeAndLinkWithNode(processSymbol, process);
    }

    @Override
    public void visit(final ASTSubProcess subProcess) {
        SubProcessSymbol subProcessSymbol = new SubProcessSymbolBuilder()
                .name(subProcess.getName())
                .build();
        subProcess.setSubProcessSymbol(subProcessSymbol);

        addToScopeAndLinkWithNode(subProcessSymbol, subProcess);
    }

    @Override
    public void visit(final ASTCallActivity callActivity) {
        CallActivitySymbol callActivitySymbol = new CallActivitySymbolBuilder()
                .name(callActivity.getName())
                .build();
        callActivity.setCallActivitySymbol(callActivitySymbol);

        addToScopeAndLinkWithNode(callActivitySymbol, callActivity);
    }

    @Override
    public void visit(final ASTTask task) {
        TaskSymbol taskSymbol = new TaskSymbolBuilder()
                .name(task.getName())
                .build();
        task.setTaskSymbol(taskSymbol);

        addToScopeAndLinkWithNode(taskSymbol, task);
    }

    @Override
    public void visit(ASTNamedGateway astGateway) {
        NamedGatewaySymbol gatewaySymbol = new NamedGatewaySymbolBuilder()
                .name(astGateway.getName())
                .build();
        astGateway.setNamedGatewaySymbol(gatewaySymbol);

        addToScopeAndLinkWithNode(gatewaySymbol, astGateway);
    }

    @Override
    public void visit(ASTNamedEvent astEvent) {
        NamedEventSymbol eventSymbol = new NamedEventSymbolBuilder()
                .name(astEvent.getName())
                .build();
        astEvent.setNamedEventSymbol(eventSymbol);

        addToScopeAndLinkWithNode(eventSymbol, astEvent);
    }


    @Override
    public void visit(final ASTFlowTarget astFlowTarget) {
        super.visit(astFlowTarget);

        // required to resolve references via enclosing scope
        astFlowTarget.setEnclosingScope(currentScope().get());
    }

    @Override
    public void visit(final ASTDataObject dataObject) {
        CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(dataObject.getType(), dataObject);

        DataObjectSymbol dataObjectSymbol = new DataObjectSymbol(dataObject.getName());
        dataObjectSymbol.setTypeSymbolRef(typeSymbolRef);
        dataObject.setDataObjectSymbol(dataObjectSymbol);
        // Create and add OCL variable symbol to scope to enable type resolving in OCL expressions
        OCLVariableDeclarationSymbol oclVarSymbol = new OCLVariableDeclarationSymbol(dataObject.getName(), typeSymbolRef);

        addToScopeAndLinkWithNode(dataObjectSymbol, dataObject);
        addToScope(oclVarSymbol);
    }

    @Override
    public void visit(final ASTError astError) {
        super.visit(astError);

/*        DataObjectSymbol errorSymbol = new DataObjectSymbol(astError.getName());

        CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(astError.getType(), astError);
        errorSymbol.setTypeSymbolReference(typeSymbolRef);

        addToScopeAndLinkWithNode(errorSymbol, astError);*/
    }

    @Override
    public void visit(final ASTEscalation astEscalation) {
        super.visit(astEscalation);

/*        DataObjectSymbol escalationSymbol = new DataObjectSymbol(astEscalation.getName());

        CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(astEscalation.getType(), astEscalation);
        escalationSymbol.setTypeSymbolReference(typeSymbolRef);

        addToScopeAndLinkWithNode(escalationSymbol, astEscalation);*/
    }

    @Override
    public void visit(final ASTMessage astMessage) {
        super.visit(astMessage);

/*        MessageSymbol messageSymbol = new MessageSymbol((astMessage.getName()));

        CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(astMessage.getType(), astMessage);
        messageSymbol.setTypeSymbolReference(typeSymbolRef);

        addToScopeAndLinkWithNode(messageSymbol, astMessage);*/
    }


    @Override
    public void visit(final ASTSignal astSignal) {
        super.visit(astSignal);

/*        DataObjectSymbol signalSymbol = new DataObjectSymbol(astSignal.getName());

        CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(astSignal.getType(), astSignal);
        signalSymbol.setTypeSymbolReference(typeSymbolRef);

        addToScopeAndLinkWithNode(signalSymbol, astSignal);*/
    }

    @Override
    public void visit(final ASTSequenceFlow astSequenceFlow) {
        super.visit(astSequenceFlow);
/*
        astSequenceFlow.setEnclosingScope(currentScope().get());*/
    }

    private CDTypeSymbolReference createTypeSymbolRef(ASTType astType, ASTNode node) {
        if (astType instanceof ASTPrimitiveType) {
            final String typeName = CDTypes.primitiveToWrapper(astType.toString());
            final CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(typeName, node);
            typeSymbolRef.setStringRepresentation(typeName);

            return typeSymbolRef;
        } else if (astType instanceof ASTSimpleReferenceType) {
            final ASTSimpleReferenceType astSimpleRefType = (ASTSimpleReferenceType) astType;
            final String typeName = Joiners.DOT.join(astSimpleRefType.getNameList());
            final CDTypeSymbolReference typeSymbolRef = createTypeSymbolRef(typeName, node);
            typeSymbolRef.setStringRepresentation(TypesPrinter.printSimpleReferenceType(astSimpleRefType));
            // Handle type arguments (generics)
            addTypeArgumentsIfPresent(typeSymbolRef, astSimpleRefType, node);

            return typeSymbolRef;
        }

        return null;
    }

    private void addTypeArgumentsIfPresent(CDTypeSymbolReference typeSymbolRef, ASTSimpleReferenceType astType, ASTNode node) {
        if (astType.isPresentTypeArguments()) {
            final List<ActualTypeArgument> actualTypeArguments = astType
                    .getTypeArguments().getTypeArgumentList().stream()
                    .map(arg -> {
                        final CDTypeSymbolReference argTypeSymbolRef = createTypeSymbolRef((ASTType) arg, node);
                        return new ActualTypeArgument(argTypeSymbolRef);
                    })
                    .collect(Collectors.toList());

            typeSymbolRef.setActualTypeArguments(actualTypeArguments);
        }
    }

    private ArtifactScope createArtifactScope(final ASTWorkflowCompilationUnit compilationUnit) {
        final String packageName = compilationUnit.getPackageName().orElse("");

        final List<ImportStatement> imports = compilationUnit
                .getImportStatementList()
                .stream()
                .map(this::createImportSymbol)
                .collect(Collectors.toList());

        return new ArtifactScope(Optional.ofNullable(scopeStack.getFirst()), packageName, imports);
    }

    private ImportStatement createImportSymbol(final ASTImportStatement imp) {
        String qualifiedImport = Names.getQualifiedName(imp.getImportList());
        // star import, otherwise resolution fails, not sure why
        return new ImportStatement(qualifiedImport, imp.isStar());
    }

    private CDTypeSymbolReference createTypeSymbolRef(final String typeName, final ASTNode node) {
        CDTypeSymbolReference typeSymbolRef = new CDTypeSymbolReference(typeName, this.getFirstCreatedScope());
        // Check if type exists in loaded CD models
        if (!typeSymbolRef.existsReferencedSymbol()) {
            Log.error(Messages.get("0xWFM1003", typeName), node.get_SourcePositionStart(), node.get_SourcePositionEnd());
        }
        return typeSymbolRef;
    }

}

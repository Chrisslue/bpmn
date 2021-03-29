/*
 * Copyright (c) 2017, MontiCore. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.bpmn.workflow._symboltable;

import de.monticore.bpmn.workflow._symboltable.WorkflowLanguageTOP;
import de.monticore.bpmn.workflow._symboltable.WorkflowModelLoader;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import ocl.monticoreocl.ocl._symboltable.OCLMethodDeclarationSymbol;
import ocl.monticoreocl.ocl._symboltable.OCLParameterDeclarationSymbol;
import ocl.monticoreocl.ocl._symboltable.OCLVariableDeclarationSymbol;

public class WorkflowLanguage extends WorkflowLanguageTOP {
    public static final String FILE_ENDING = "wfm";

    public WorkflowLanguage() {
        super("BPMN Language", FILE_ENDING);
    }

    @Override
    protected WorkflowModelLoader provideModelLoader() {
        return new WorkflowModelLoader(this);
    }

    @Override
    protected void initResolvingFilters() {
        super.initResolvingFilters();

        // Add resolving filters for OCL
        addResolvingFilter(new CommonResolvingFilter<OCLVariableDeclarationSymbol>(OCLVariableDeclarationSymbol.KIND));
        addResolvingFilter(new CommonResolvingFilter<OCLMethodDeclarationSymbol>(OCLMethodDeclarationSymbol.KIND));
        addResolvingFilter(new CommonResolvingFilter<OCLParameterDeclarationSymbol>(OCLParameterDeclarationSymbol.KIND));
    }

}

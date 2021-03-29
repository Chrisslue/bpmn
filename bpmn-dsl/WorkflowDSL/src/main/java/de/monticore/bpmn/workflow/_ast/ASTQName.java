package de.monticore.bpmn.workflow._ast;

import com.google.common.collect.Lists;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.Splitters;

import java.util.List;

public class ASTQName extends ASTQNameTOP {

    protected ASTQName() {
        super();
    }

    protected ASTQName(final List<String> parts) {
        super(parts);
    }

    public ASTQName(final String qualifiedName) {
        super();
        setQualifiedName(qualifiedName);
    }

    public String getQualifiedName() {
        return Names.getQualifiedName(getPartList());
    }

    public void setQualifiedName(final String qualifiedName) {
        final List<String> parts = Lists.newArrayList(Splitters.DOT.split(qualifiedName));
        setPartList(parts);
    }

    public String getSimpleName() {
        return getPart(sizeParts() - 1);
    }

}

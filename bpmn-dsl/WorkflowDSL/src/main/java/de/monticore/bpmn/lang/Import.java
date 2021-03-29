package de.monticore.bpmn.lang;

public class Import {

    private final String qualifiedName;

    private final boolean isStar;

    public Import(String qualifiedName, boolean isStar) {
        this.qualifiedName = qualifiedName;
        this.isStar = isStar;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public boolean isStar() {
        return isStar;
    }

}

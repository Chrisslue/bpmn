package de.monticore.bpmn.workflow._symboltable;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;

public class SymbolTablePrinter {

    private String content = "";

    private int indention = 0;

    private String indent = "";

    public String getContent() {
        return content;
    }

    public SymbolTablePrinter print(Scope scope) {
        return  this.printScope(scope);
    }

    private SymbolTablePrinter printScope(Scope scope) {
        String scopeName = scope.getName().orElse("<anonymous>");
        String scopeType = scope.getClass().getSimpleName();

        println(scopeType + "(" + scopeName + ")");
        indent();
        scope.getLocalSymbols().forEach((name, symbols) -> symbols.forEach(this::printSymbol));
        scope.getSubScopes().forEach(this::printScope);
        unindent();

        return this;
    }

    private void printSymbol(Symbol symbol) {
        String symbolName = symbol.getName();
        String symbolKind = symbol.getClass().getSimpleName();

        println(symbolKind + "(" + symbolName + ")");
    }

    private void print(String s) {
        content += (indent + s);
        indent = "";
    }

    private void println(String s) {
        content += (indent + s + "\n");
        indent = "";
        calcIndention();
    }

    private void indent() {
        indention++;
        calcIndention();
    }

    private void unindent() {
        indention--;
        calcIndention();
    }

    private void calcIndention() {
        indent = "";
        for (int i = 0; i < indention; i++) {
            indent += "  ";
        }
    }

}

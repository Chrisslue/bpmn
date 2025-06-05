 /* (c) https://github.com/MontiCore/monticore */ 
package de.monticore.bpmn;

/** Utilities for types. */
public class TypesHelper {

  public static boolean isCollection(final String type) {
    // TODO better type checking, e. g. "java.util.List<Object>"
    return type.startsWith("Collection") || type.startsWith("List") || type.startsWith("Set");
  }
}

package de.monticore.bpmn.wf2lts;

import java.util.function.Function;

public interface NamingStrategy<T> extends Function<T, String> {

}
<!-- (c) https://github.com/MontiCore/monticore -->

[monticore-website]: http://www.monticore.de

# BPMN

[[_TOC_]]

The Business Process Model and Notation (BPMN) is a standardized language 
for modeling business processes and workflows. 
This project implements a textual notation of the BPMN language with 
[MontiCore][monticore-website] and provides comprehensive tooling for 
working with the language.


Note that the repository contains more than just the `BPMN-DSL`,
which is the actual Gradle project implementing the BPMN language. 
It is recommended to only open the project under `bpmn/bpmn-dsl` in your IDE, 
not the entire repository.

## Build BPMN-DSL

* Install Gradle 7.5
* Install Java 11
* Build with command `gradle build`

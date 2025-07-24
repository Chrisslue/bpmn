<!-- (c) https://github.com/MontiCore/monticore -->

[monticore-website]: http://www.monticore.de

# Business Process Model and Notation (BPMN)

[[_TOC_]]

The Business Process Model and Notation (BPMN) is a standardized language 
for modeling business processes and workflows. 
This project implements the Workflow DSL, a textual notation of the BPMN 
language with [MontiCore][monticore-website] and provides comprehensive 
tooling for working with the language.

A detailed documentation for **language engineers** using or extending the
Workflow DSL is located 
**[here](WorkflowDSL/src/main/grammars/de/monticore/bpmn/Workflow.md)**.

## Build the Project

* Install Gradle 7.5
* Install Java 11
* Build with command `gradle build`

## BPMN Tool

After building the project, a CLI tool can be found
[here](WorkflowCLI/target/libs/BPMN.jar).

### Actions and Parameters of the Tool

The tool provides quite a number of executable actions and configurable
parameters.
The possible options are:

| Option                     | Explanation                                                                                                                |
|----------------------------|----------------------------------------------------------------------------------------------------------------------------|
| `-h,--help`                | Prints this help dialog.                                                                                                   |
| `-i,--input <file>`        | Reads the source file (mandatory) and parses the contents.                                                                 |
| `--path <dirlist>`         | Sets the artifact path for imported symbols, space separated (default is: `.`).                                            |
| `-pp,--prettyprint <file>` | Prints the AST to stdout or the specified file (optional).                                                                 |
| `-s,--symboltable <file>`  | Serializes the symbol table of the given artifact.                                                                         |
| `-v,--version`             | Prints version information.                                                                                                |
| `-ref,--reference <file>`  | Parses the file as a reference process model and checks if the the input process model specified by `-i` is conform to it. |
| `-m,--map <string>`        | Specify the names of stereotypes that are used as incarnation mappings in the concrete model. Default : 'incarnates'       |

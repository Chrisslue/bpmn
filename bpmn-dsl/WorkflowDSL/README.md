# BPMN

## About

Textual BPMN notation with comprehensive tooling, made in [MontiCore][monticore-website]-land.

[Overview of the supported BPMN elements](../../../06.Dokumentation/BPMN20-Support.md)

### Built With

- [MontiCore][monticore-website] (version 5.0.2)
- [Google Guava][guava-website]
- [JGraphT][jgrapht-website]
- [LoLA 2.0][lola-website]

## Getting Started

### Prerequisites

- [Java 8 (JDK)][jdk-8-website], untested with Java 9 or newer
- [Maven][maven-website]
  - Access to the [Nexus repository][nexus-website] (make sure to set your credentials in `~/.m2/settings.xml`)
- [LoLA 2.0][lola-website] for behavioral analysis (optional)
- [GraphViz][graphviz-website] for generating images (optional)

### Installation

To build this project run

```bash
mvn clean install
```

### Documentation

To generate the JavaDoc documentation for this project, run

```bash
mvn generate-resources javadoc:javadoc
```

This creates a directory `/target/site` with a file `index.html`. Open the `index.html` file in your browser to browse the documentation.

To create a ZIP file containing the documentation, run

```bash
mvn generate-sources javadoc:javadoc assembly:single
```

## Usage

The `WorkflowTool` is the main class to load and manipulate BPMN models.

A basic example:

```java
ModelPath modelPath = new ModelPath(Paths.get("models/"));
WorkflowCoCoChecker checker = WorkflowCoCos.getBasicChecker();

ASTWorkflowCompilationUnit ast = new WorkflowTool()
    .loadModel("demo.Example", modelPath)
    .checkCoCos(checker)
    .transform(new ReplaceSubProcessWithTaskTrafo())
    .checkCoCos(checker)
    .exportXml(Paths.get("out/"))
    .writeAuxiliaryModels(Paths.get("aux/"))
    .getAst();
```

It executes the following steps:

1. Load the model with the qualified name `demo.Example` from the model path `models/`
2. Check basic context conditions
3. Transform the AST, e.g, replace sub-processes by (atomic) tasks
4. Check basic context conditions, again
5. Export the model into the BPMN 2.0 XML exchange format
6. Write auxiliary models (for manual inspection)
7. Get the AST

*For more details, please refer to the [JavaDoc documentation](#documentation).*

## Project Structure

The project tree with the most important classes:

```text
./src
├── main
│   ├── grammars
|   │   └── de/monticore/bpmn
|   │       └── Workflow.mc4                              # BPMN grammar file
│   └── java
│       └── de/monticore/bpmn
│           ├── analysis
│           │   ├── graph
│           │   │   └── WorkflowGraphConverter.java       # BPMN to graph
│           │   ├── lola
│           │   │   └── LoLaChecker.java                  # LoLA proxy
│           │   └── petrinet
│           │       ├── WorkflowNetConverter.java         # BPMN to WF-Net
│           │       └── WorkflowNet.java                  # WF-Net
│           ├── cocos
│           ├── collectors
│           ├── lang
│           │   └── WorkflowTool                          # BPMN Tool
│           ├── trafos
│           │   └── WorkflowTransformation.java           # BPMN abstract trafo
│           ├── utils
│           ├── visitors
│           ├── workflow                                  # AST extensions
│           └── xml
│               ├── WorkflowXmlSerializer.java            # BPMN to XML (main)
│               └── WorkflowXmlSerializerVisitor.java     # BPMN to XML
└── test
    ├── java
    │   └── de/monticore/bpmn
    │       ├── cocos
    │       |   ├── AbstractCoCoTest.java
    │       |   └── ...                                   # CoCo Tests
    │       └── AbstractTest.java
    └── resources
        └── de/monticore/bpmn
```

## Contact

Erik Müller - erik.mueller@rwth-aachen.de

[monticore-website]: http://www.monticore.de
[jgrapht-website]: https://jgrapht.org
[guava-website]: https://guava.dev
[lola-website]: http://service-technology.org/lola/
[maven-website]: https://maven.apache.org
[nexus-website]: https://nexus.se.rwth-aachen.de
[jdk-8-website]: https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[graphviz-website]: https://www.graphviz.org

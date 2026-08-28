# User Manual: bpmn4cd (sleProject)

In the following, we will discuss how to build the project, run the provided tests, 
and how to utilize the infrastructure to integrate additional class diagrams
to represent data of BPMN processes.

## Build & Run Tests

For the Gradle build, the setup requires a JDK installation according to the version
stated in `build.gradle`.
Note that there is no need for a local Gradle installation.
To perform the Gradle build, simply execute

```
gradle build
```

which builds all three modules (`WorkflowDSL`, `WorkflowConformance`, `WorkflowCLI`).

As mentioned in the [documentation](/doc/sleProject/documentation.md), the 
results of the `bpmn4cd` project are contained in the `WorkflowDSL` module.
The corresponding Gradle setup uses the `cd2pojo` plugin to create symbol tables
from class diagrams located in `WorkflowDSL/src/test/cd2pojo/`.
This symbol table is then loaded during the `loadModel(...)` call in the
tests we implemented.

To only run the test cases relevant for the `bpmn4cd` project, it suffices
to execute:

```
gradle testSleProject
```

## Integrating CD in a BPMN Process

To access the static data of a CD in a BPMN process, the current setup requires
the `.cd` file to be located at
```
WorkflowDSL/src/test/cd2pojo/<some.package>/SomeDiagram.cd
```

After running the Gradle build by executing
```
gradle build
```
the infrastructure creates the corresponding symbol table under
```
build/cd2pojo/<sourceSet>/symbols/<package/as/path>/<DiagramName>/
```

Next, we need to correctly place the process which should use the CD, and its
created symbol table, to model the data flow.
For this, place the process in
```
WorkflowDSL/src/test/resources/<some/package>/SomeProcess.wfm
```
Note that we assume that the process is already modeled as a `.wfm` file.

Finally, to reference the data types created based on the CD, we add imports at 
the beginning of the process:

```
import de.monticore.bpmn.someProject.Diagram.*;

process SomeProcess {
  data some:Some;
  ...
}
```

From here on you can utilize Boolean operations defined in the CD directly
as gateway conditions in the process.

To check whether the connection between the CD and the BPMN process works as expected,
it might be helpful to create a JUnit test, which loads the symbol table and the
process as follows:

```java
WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
    "target/cd2pojo/test/symbols/<some/package>/SomeDiagram"));

loadModel("<some.package>.SomeProcess");
```

In case of a successful integration, no errors are logged.
This can be checked by `Log.getFindings()`.

## Using the CLI Directly

Another way to integrate the symbol table of the class diagram into the BPMN process,
it is also possible to directly utilize the command line interface as follows:
```
java -jar <WorkflowCLI jar> -i path/to/YourProcess.wfm -path path/to/symboltable/root
```

Here, the CLI offers some additional flags which might be useful:

| Flag | What it does |
|---|---|
| `-i <file>` | The process model to load. Required - the tool exits with an error if this is missing. |
| `-path <dir...>` | Adds a directory to the symbol path, same idea as `getSymbolPath().addEntry(...)` in a test. Can be given more than once; defaults to `.` if omitted. |
| `-pp [file]` | Pretty-prints the parsed model. Pass a file to write to it, or leave it empty to print to stdout. |
| `-s <file>` | Serializes the model's symbol table to `<file>`. |
| `-ref <file>` | Loads `<file>` as a reference model and checks the `-i` model for conformance against it. |
| `-m <stereotype>` | Name of the stereotype used to encode the incarnation mapping when checking conformance. Defaults to `incarnates`. |
| `-h` | Prints the full option list. |
| `-v` | Prints the tool version. |
*Table 1: Useful CLI Flags*

## Common Troubleshooting Issues

**"Cannot find symbol X"** 
This most likely indicates that there is some reference in the process that 
cannot be resolved in the corresponding symbol table of the CD. 
It makes sense to double-check for spelling mistakes or to make sure that the
most recent symbol table is used.

**The CD file parses, but none of its classes resolve** 
Most often this is caused by the `package` declared in the `.cd` file not matching
the directly it is located in.

**A test terminates before reaching `Log.getFindings()`.** 
This is caused by the fail-quick functionality implemented in the logger.
Note that this behavior can be avoided by extending `AbstractTest`, which calls
`Log.enableFailQuick(false)` during the setup.

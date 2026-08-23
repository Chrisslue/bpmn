# User Manual: bpmn4cd (sleProject)

This manual describes how to build the project, run our proof-of-concept
tests, and how to connect your own class diagram (CD) to a BPMN process model
the same way our `TimeManagement` example does.

## Prerequisites

- A JDK compatible with the existing MontiCore/BPMN build (see the root
  `build.gradle` for the exact version).
- No local Gradle installation is required, as the repository ships the
  Gradle wrapper.

## Building the Project

From the repository root, run:

```
gradle build
```

This builds all three subprojects (`WorkflowDSL`, `WorkflowConformance`,
`WorkflowCLI`) and, as part of the `WorkflowConformance` build, invokes the
`cd2pojo` plugin to generate a symbol table from every `.cd` file found under
`WorkflowConformance/src/test/cd2pojo/`. The generated symbol tables are placed
under `WorkflowConformance/build/cd2pojo/...` and are what our tests later load
via `WorkflowMill.globalScope().getSymbolPath()`.

## Running Only the sleProject Tests

Rather than executing the full, pre-existing BPMN test suite, we added a
dedicated Gradle task that only runs our sleProject-related JUnit tests:

```
gradle testSleProject
```

This task is registered both at the root level and inside
`WorkflowConformance/build.gradle`, and internally runs the same JUnit engine
as the regular `test` task, just scoped to our test classes.

## Connecting a CD to a BPMN Process: Step by Step

The core idea of this project is that a BPMN process (`.wfm` file) can declare
`data` items whose types come from a class diagram, and can use the operations
declared on those CD classes directly inside gateway conditions. The
`TimeManagement` example demonstrates the full pipeline; the following steps
describe how to create your own.

### 1. Write a Class Diagram

Place a `.cd` file under
`WorkflowConformance/src/test/cd2pojo/<your.package.name>/YourDiagram.cd`.
The package declared inside the file must match the directory it is placed
in, following standard MontiCore conventions. For example, our
`TimeManagement.cd` declares classes like `TimeSlot` with typed attributes
(`LocalDateTime startTime`, `String project`, ...) and boolean operations
(`hasEndTime()`, `isVacation()`, ...) that are later used as guard conditions.
Note that these operations only need a **signature** in the CD; they are not
implemented there, as the CD only contributes types and names to the symbol
table (see [Limitations](limitations.md) for what this means in practice).

### 2. Build the Project to Generate the Symbol Table

Running `gradle build` (or at least the `WorkflowConformance` module's build)
triggers `cd2pojo`, which turns your `.cd` file into a symbol table on disk,
following the path convention
`build/cd2pojo/<sourceSet>/symbols/<package/as/path>/<DiagramName>/`.

### 3. Write a BPMN Process That References the CD

Place a `.wfm` file under
`WorkflowConformance/src/test/resources/<your/package/path>/YourProcess.wfm`.
Import the CD-generated types with a wildcard import, e.g.:

```
import de.monticore.bpmn.conformance.sleProject.TimeManagement.*;

process TimeManagement {
  data slots:TimeSlotList;
  data chosenSlot:TimeSlot;
  ...
}
```

From here on, you can use the imported types both to type `data` items (as
above) and inside gateway conditions, e.g.
`[chosenSlot.hasEndTime()] SomeTask;`, exactly as our example does.

### 4. Load the Model in a Test

To actually connect the generated symbol table to your process model at
parse time, register the symbol table's location before loading the model,
as shown in `SleProjectTest.java`:

```java
WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
    "target/cd2pojo/test/symbols/<your/package/path>/YourDiagram"));

loadBPMN("<your.package.name>.YourProcess", true);
```

`loadBPMN(...)` is a convenience method on `BPMNConformanceUtils` that mirrors
the model-loading pipeline of the `WorkflowCLI` tool (imports, scope creation,
symbol table completion, context condition checking), but is exposed as a
plain static method so it can be called directly from JUnit tests instead of
only from the command line.

### 5. Interpret the Result

- If every type and operation referenced in the `.wfm` file is resolvable in
  the loaded symbol table, `Log.getFindings()` is empty and no errors are
  reported, as in our `loadsSymbolsFromCD` test.
- If a referenced type or operation cannot be resolved, e.g., because it does
  not exist in the CD or the wrong symbol table was loaded, the process still
  parses, but `Log.getFindings()` contains one error `Finding` per
  unresolvable symbol, as demonstrated by our `failsForIncompleteSymbolsFromCD`
  test (see the [technical documentation](documentation.md#bad-case-failsforincompletesymbolsfromcd)
  for the exact error format).
  By default the `Log` terminates the whole program on the first error; if you
  want to inspect all findings instead (as our bad-case test does), call
  `Log.enableFailQuick(false)` before loading the model.

## Using the Command-Line Tool Directly

Independently of the test-based workflow above, `WorkflowCLI`'s
`WorkflowTool` can also be invoked directly to parse, pretty-print, or
conformance-check a single process model against a reference model:

```
java -jar <WorkflowCLI jar> -i path/to/YourProcess.wfm -path path/to/symboltable/root
```

Relevant options include `-pp <file>` to pretty-print the parsed model,
`-s <file>` to serialize its symbol table, and `-ref <file>` together with
`-m <stereotype>` to check conformance against a reference process via
incarnation stereotypes. Run the tool with `-h` for the full list of options.

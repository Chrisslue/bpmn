# User Manual: bpmn4cd (sleProject)

How to build the project, run our tests, and hook up your own class diagram
(CD) to a BPMN process the same way `TimeManagement` does.

## Build & Run Tests

You need the JDK version the root `build.gradle` expects; the Gradle wrapper
is checked into the repo, so no local Gradle install is required.

```
gradle build
```

builds all three modules (`WorkflowDSL`, `WorkflowConformance`, `WorkflowCLI`).
Our sleProject code lives inside `WorkflowDSL`, which is also where the
`cd2pojo` plugin runs as part of that build - it turns every `.cd` file under
`WorkflowDSL/src/test/cd2pojo/` into a symbol table on disk
(`WorkflowDSL/build/cd2pojo/...`). That symbol table is what `loadModel(...)`
reads from later when a test tries to resolve a type.

If you only want our tests and not the much larger, pre-existing BPMN test
suite:

```
gradle testSleProject
```

The task is registered in `WorkflowDSL/build.gradle` and filters down to
`SleProjectTest`; the root project just forwards to it, so running it from
the repository root works the same way.

## Wiring a CD into a BPMN Process

It helps to have `TimeManagement.cd` and `TimeManagement.wfm` open side by
side while reading this, since the steps below are easier to follow with a
concrete example in front of you than in isolation.

Start with the class diagram: drop a `.cd` file into
`WorkflowDSL/src/test/cd2pojo/<your.package>/YourDiagram.cd`, with the
package declared inside matching the folder it sits in. Give your classes
typed attributes and, if you'll need conditions later, boolean operations
like `hasEndTime()`. One thing that might confuse here: these operations
only need a signature, not a body - the CD format has no way to express what
they actually do. `cd2pojo` only pulls names and types into the symbol
table, nothing more, so don't expect the tooling to check whether
`durationGreaterThanTenHours()` is actually implemented consistently with
its name anywhere.

Build once (`gradle build`) and the symbol table shows up under
`build/cd2pojo/<sourceSet>/symbols/<package/as/path>/<DiagramName>/`.

Now write the process. Put a `.wfm` file under
`WorkflowDSL/src/test/resources/<your/package>/YourProcess.wfm` and import
the generated types with a wildcard:

```
import de.monticore.bpmn.sleProject.TimeManagement.*;

process TimeManagement {
  data slots:TimeSlotList;
  data chosenSlot:TimeSlot;
  ...
}
```

From here you can use those types both to type `data` items, as above, and
inside gateway conditions - `[chosenSlot.hasEndTime()] SomeTask;`, for
instance.

To actually connect the two at parse time, write a JUnit test extending
`AbstractTest` and point the symbol path at your generated symbol table
before loading the model:

```java
WorkflowMill.globalScope().getSymbolPath().addEntry(Path.of(
    "target/cd2pojo/test/symbols/<your/package>/YourDiagram"));

loadModel("<your.package>.YourProcess");
```

If everything resolves, `Log.getFindings()` comes back empty - that's what
`loadsSymbolsFromCD` checks for. If something doesn't resolve (wrong symbol
table registered, a typo, a class missing from the CD), you get back one
error `Finding` per unresolvable symbol rather than the whole run just
aborting. That's not something you have to configure yourself: `AbstractTest`
already disables fail-quick logging for every test that extends it in its
`setUp()`, precisely so these findings can be inspected instead of crashing
the test on the first error. `failsForIncompleteSymbolsFromCD` is the
reference example if you want to write a similar negative test for your own
CD.

## Using the CLI Directly

You don't have to go through JUnit at all. `WorkflowCLI`'s `WorkflowTool`
parses, pretty-prints, and conformance-checks models straight from the
command line:

```
java -jar <WorkflowCLI jar> -i path/to/YourProcess.wfm -path path/to/symboltable/root
```

`-pp <file>` pretty-prints the parsed model, `-s <file>` dumps its symbol
table, and `-ref <file>` together with `-m <stereotype>` checks conformance
against a reference model via incarnation stereotypes. Run it with `-h` if
you forget an option.

## Useful CLI Flags

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

## Common Troubleshooting Issues

**"Cannot find symbol X" for a type you're sure exists in your CD.** Almost
always means the symbol table for that CD was never registered, not that the
CD is wrong. Two usual causes: you forgot to `gradle build` (or build the
`WorkflowDSL` module) after adding or changing the `.cd` file, so
`build/cd2pojo/...` is stale or missing; or the path you passed to
`getSymbolPath().addEntry(...)` / `-path` doesn't actually match where
`cd2pojo` wrote the symbol table for your package.

**The CD file parses, but none of its classes resolve.** Check that the
`package` declared inside the `.cd` file matches the directory it's placed
in. MontiCore derives the expected location from the package name, so a
mismatch here silently breaks resolution rather than producing an obvious
error.

**A test throws instead of letting you inspect `Log.getFindings()`.** This
is the fail-quick logger terminating on the first error - the default
behavior outside of `AbstractTest`. Extending `AbstractTest` avoids this
automatically, since its `setUp()` calls `Log.enableFailQuick(false)` for
you. If you're loading a model somewhere that doesn't go through
`AbstractTest`, you'll need to call that yourself before parsing.

**Gateway condition referencing a CD operation doesn't resolve, even though
the operation is spelled correctly.** Double-check the return type and that
you're calling it on the right `data` item - the symbol table only matches
on name and signature, so a `TimeSlot` operation called on a `data` item
typed as something else won't resolve, and the resulting error can look
identical to a genuinely missing symbol.

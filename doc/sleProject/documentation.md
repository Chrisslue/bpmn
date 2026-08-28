# Project Overview

## Goal

The goal of this project was to evaluate whether and to which extent the already
existing BPMN implementation is able to handle the combination of the
CD and the BPMN DSLs.
The project is motivated by the fact that both DSLs were largely developed
independently
even though combining both is useful for real-world business cases.
For this, we agreed on first defining an extensive business case and then
create
the following artifacts:

1. A [BPMN](https://bpmn.io/) process that handles the flow of our business case
2. A class diagram representing data required for the process
3. Translations of both to the corresponding DSLs
4. A short test suite to verify the correctness of the integration

# Organization

## Team (alphabetically)

- Johannes Kurth
- Peter Lindner
- Christoph Lütticke
- Abdullah Rehman

**Supervisor**: Sedat Cakici

## Tools

1. [BPMN.io](https://bpmn.io/) Is an open source project supported by the
   creators of [Camunda](https://camunda.com/) It allows for easy creation of
   BPMN diagrams through an online editor. We used it mainly for brainstorming
   and designing the process.
2. [Discord](https://discord.com/) Is a voice and text communication platform.
   We used it for our meetings and pair programming sessions.

## Workflow

During the time of the project we met on Discord regularly.
In these meetings we discussed the current state of the project and next steps.
After every meeting with the supervisor, we compared notes and persisted them in
a thread as not to lose track of relevant feedback and decisions.

In the beginning, we mainly worked together, designing the process and
translating it to the DSLs.
Due to time constraints in the exam period, the last few weeks of the project,
mainly working on the Gradle build tool, and the test cases were done independently 
and split as evenly as possible.

# TimeManagement Process

![process.svg](img/process.svg)
*Process 1: Main process to handle the business case*
![subprocess.png](img/subprocess.png)
*Process 2: Subprocess to validate the user input*

## Business Case


In our main process given above, an `Employee` wants to log working hours or
apply
to approve an upcoming vacation.
For this, we have two stakeholders, being the `Employee` and the `HR`
representative.
In the beginning the `Employee` opens the dashboard, which shows their already
logged timeslots. These timeslots are
persisted on a database and locally represented as a list of `TimeSlot` objects.
Upon choosing an empty timeslot with the intention to either log time to a
project
or mark it as vacation, the system creates a new `TimeSlot` object called
`slot`.
Now, the process branches depending on whether `chosenSlot.hasEndTime()` is
`true` or `false`. Here, `chosenSlot` refers to the previously chosen `TimeSlot`
the `Employee` selected from the dashboard.
In case the `chosenSlot` already has an end time, i.e.,
`chosenSlot.hasEndTime()`
Returns `true`, the process pre-fills the start time of the created `slot`.
Otherwise, it continues with the empty `slot`.
Next, the `Employee` manually inserts the information of the new `TimeSlot`, and
confirms the input or aborts the creation.
As the order of the input does not really matter, the process relies
on parallel user tasks here.
Afterward, the given user input gets validated in the subprocess illustrated
above.
Here, the input data gets both checked for consistency, e.g., that the times
represent a valid `TimeSlot` and additional business requirements.
In case of valid inputs, the process continues depending on whether the chosen
`project` equals to `vacation` or not.
In the former, the process redirects the vacation request to `HR`, and in the
latter
it directly persists the created `TimeSlot` to the database.
Otherwise, i.e., in the case of invalid input, the process shows an
`ErrorMessage`
And redirects the `Employee` back to correct the corresponding information.
If there occurs an error while persisting the data, an `ErrorMessage` is
returned
to the user and the process leads back to the user input stage.
In case of a vacation request, `HR` needs to manually verify the request and
create a `VacationRequestDecision`.
For traceability, this decision also gets persisted on the database.
Depending on whether the request got approved or not, the `TimeSlot` gets either
persisted or the process ends.

## Design Decisions

- The workflow of the process is inspired by Macoco, the internal worklog tool
  of the i3 chair.
- As one of the main goals of the project was to test the integration of CDs
  and BPMN, we decided to use as many elements of the BPMN language as possible.
  This would allow for more coverage. However, this also resulted in some of the
  more unconventional choices and style of the BPMN
   - To have a parallel gateway, we split up the user input into four parallel
     tasks
   - To add a subprocess, and for better readability of the process, we decided
     to place the validation of
     the input into a subprocess, as depicted in the graphics above.
   - To integrate a second actor into the process, we require a timeslot linked
     to the 'vacation' project to be approved by `HR` before.
   - For faults caused by either user input, e.g., aborting a task, or technical
     causes, 
     we relied on intermediate throw events to avoid using too many gateways
     which
     do not really contribute or further clarify the business case.

## Class Diagram

![CD.png](img/CD.png)

*Class Diagram 1: Data model of the process*

When modeling the class diagram, we focused on staying close to the vocabulary of
the process to make it easier to understand the data flow.
In the CD, `TimeSlot` represents a certain time slot that can be booked to some
project or vacation, which we modeled as a dedicated vacation project.
As a `TimeSlot` is not required to have an `endTime`, we connected them by a
`[0..1]`
Association.
The Boolean operations `hasEndTime()`, `isVacation()`,
`endTimeBeforeStartTime()`,
`insufficientPermissions()` and `durationGreaterThanTenHours()` each correspond
to gateway conditions in the process.
For the `TimeSlotList` we added an association.
The additional classes `VacationRequestDecision` and `ErrorMessage` are kept as
small as possible as they only model side effects that are not central to the
business case.
The `Database` class is only included to enable the process model to resolve the
symbol `store database:Database`.

# Technical Details

## Project Structure

As we forked the existing BPMN repository of MontiCore, our project-specific
results are added to the predefined project structure.
However, for completeness, we give a short overview of the complete repository,
which is made up of three modules:

- `WorkflowDSL` contains the MontiCore grammar in form of [
  `Workflow.mc4`](/WorkflowDSL/src/main/grammars/de/monticore/bpmn/Workflow.mc4),
  which defines a process language that mimics the BPMN standard.
- `WorkflowConformance` contains implementation that allows to check whether
  a process model conforms to some reference model.
- `WorkflowCLI` contains the `WorkflowTool.java`, which implements the
  command-line entry point for parsing, pretty-printing and conformance-checking
  models. Here, we indirectly use it via the `BPMNConformanceUtils` to load models.

Our contributions can be found at the following places in the `WorkflowDSL`
module:

- `TimeManagement.cd`: `src/test/cd2pojo/de.monticore.bpmn.sleProject/`
- `TimeManagementIncomplete.cd`:
  `src/test/cd2pojo/de.monticore.bpmn.sleProject/`
- `TimeManagement.wfm`: `src/test/resources/de/monticore/bpmn/sleProject/`
- `TimeManagementIncomplete.wfm`:
  `src/test/resources/de/monticore/bpmn/sleProject/`
- `SleProjectTest.java`: `src/test/java/de/monticore/bpmn/sleProject/`

## Gradle Setup

Initially we assumed that the 
[existing BPMN repository](https://github.com/MontiCore/bpmn) would
already contain functionality to parse and check BPMN models, including CD files, because this functionality
was already implemented.
Therefore, we decided to fork it, create a new [GitLab repository](https://git.rwth-aachen.de/se-student/ss26/lectures/sle/projects/bpmn4cd)
and add our test cases to the existing test suite.
However, we later found out that the existing solution required us to place
precompiled CD-symbol files into the filepath and add them manually. Therefore,
we extended the Gradle pipeline to automatically generate the symbol files from
class diagramms automatically using the preexisting cd2pojo plugin.
We chose this one over other cd-parsers because it generates java classes. This
is not directly needed for our project, but it is set up in such a way that it
generates the classes (and symbol files) before the java code is run, ensuring
the correct execution order.
In the tests, we only have to add the folder or generated files to the symbol
path.

As usual, the project can be built by executing:

```
gradle build
```

To execute only the tests created for our proof-of-concept, we created a Gradle
task that can be executed by:

```
gradle testSleProject
```

## Test Cases

### Happy Case: loadsSymbolsFromCD

This test case loads the business process `TimeManagement` via the
`BPMNConformanceUtils`, and ensures that no errors occur during the processing.

### Bad Case: failsForIncompleteSymbolsFromCD

The bad test case is very similar to the happy case but loads the BPMN model
against the symbol tables of an incomplete class diagram. The `TimeManagementIncomple.cd`
is identical to the `TimeManagement.cd` but misses the `TimeSlotList` class required
by the process. As the `TimeManagementIncomplete.wfm` declares `data slots:TimeSlotList;`,
loading it against the incomplete symbol tables must fail because the type cannot be resolved.
The test starts---after initialization---by calling `Log.enableFailQuick(false)`. This call
disables the fail quick mode of the logger which is necessary because by default the logger
terminates the program if an error is logged. This would contradict the purpose of the test
because the results and expected error could not be inspected anymore.
After loading the model, the test assures that only one error occurred during loading
and confirms that the symbol `TimeSlotList` could not be found by validating the error message.
By doing so, we confirm two things: First, our CD-to-symbol-table pipeline is actually
consulted while resolving BPMN data types and an unresolvable type is not silently ignored.
Second, missing symbols are reported as a single, precise error and do not lead to a series
of further follow-up errors.

# Project Evaluation

In the following, we discuss the limitations of the results produced throughout
our project and the whole BPMN project itself with respect to the goals
initially
agreed on.
We hope this will be useful for follow-up projects.

## Goal Achievement and Limitations

We successfully defined a business case, created the corresponding class
diagrams,
an extensive BPMN process and translated them to the existing DSLs.
Therefore, goals 1-3, as defined on
the [kick-off slides](/doc/sleProject/Kickoff.SLE26.bpmn4cd.pdf),
were fully achieved.
Goal 4, i.e., the access to the data given by class diagrams through the
process,
was achieved partially; in more detail:

- It is possible to realize static, symbol-table-level access to `data` items in
  the corresponding WFM file. These items and their operations defined in the
  class diagram may be referenced in, for example, gateway conditions and are
  correctly resolved and type-checked against the generated symbol table.
  We illustrate this by our two test cases detailed below.
- However, as there is no executable process engine, we were not able to clarify
  whether it is possible to access the data during runtime. Creating such an
  executable process engine is out of scope for this project. Thus, this
  might present an opportunity for future work.

# Usage of AI Tools

We used Codex for the first translation of our process from the BPMN format to
the WFM encoding. However, we were required to manually adjust the WFM file as
we were not completely satisfied with the result.
We did all other kinds of implementation and writing ourselves without the
assistance of any AI tooling.

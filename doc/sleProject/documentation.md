# Project Overview

## Goal

The goal of this project was to evaluate whether and to which extent the already
existing BPMN implementation is able to handle the communication between the
CD and the BPMN DSLs.

# Technical Details

## Project and Gradle Setup

To avoid replicating the Gradle infrastructure of the already existing project
and to simplify the migration into the existing MontiCore codebase later, we
decided to create a fork of the [existing BPMN repository](https://github.com/MontiCore/bpmn) 
instead of working in the created [GitLab repository](https://git.rwth-aachen.de/se-student/ss26/lectures/sle/projects/bpmn4cd).
In addition to the already existing Gradle setup and dependencies, we added 
the `cd2pojo` dependency to be able to process CDs directly.

As usual, the project can be built by executing:
```
gradle build
```

To execute only the tests created for our proof-of-concept, we created a Gradle
task that can be executed by:
```
gradle testSleProject
```

## Project Structure



# Organizational Details

## Team (alphabetically)

- Johannes Kurth
- Peter Lindner
- Christoph Lütticke
- Abdullah Rehman

**Supervisor**: Sedat Cakici

## General Workflow

Most of our work is a result of meeting on Discord and working on the problems at the
same time through pair programming. This allowed us to both keep all members up-to-date 
and also ensure that the time committed per person is spread evenly among the team members.
During the meetings we took notes to guarantee that we meet all expectations of our
supervisor.

# WorkflowManagement Process

![process.svg](img/process.svg)
*Process 1: Main process to handle the business case*
![subprocess.png](img/subprocess.png)
*Process 2: Sub-process to validate the user input*

## Business Case

In our main process given above, an `Employee` wants to log working hours or apply 
for approval of an upcoming vacation.
For this, we have two stakeholders, being the `Employee` and the `HR` representative.
In the beginning the `Employee` opens the dashboard, which shows their (we use
gender-neutral language here) already logged timeslots. These timeslots are 
persisted on a database and locally represented as a list of `TimeSlot` objects. 
Upon choosing an empty timeslot with the intention to mark it as vacation, the 
system creates a new `TimeSlot` object.
**TODO**: Explain `chosenSlot.hasEndTime()` branching.
Next, the `Employee` manually inserts the information of the new `TimeSlot`, and
confirms the input or aborts the creation.
As the order of the input does not really matter, for this step the process relies 
on parallel user tasks.
Afterward, the given user input gets validated in the sub-process illustrated above.
Here, the input data gets both checked for consistency, e.g., that the times
represent a valid `TimeSlot` and additional business requirements.
In case of valid inputs, the process continues depending on whether the chosen 
`project` equals to `vacation` or not. 
In the former, the process redirects the vacation request to `HR`, and in the latter 
it directly persists the created `TimeSlot` to the database.
Otherwise, i.e., in the case of invalid input, the process shows an `ErrorMessage` 
and redirects the `Employee` back to correct the corresponding information.
If there occurs an error while persisting the data, an `ErrorMessage` is returned
to the user and the process leads back to the user input stage.
In case of a vacation request, `HR` needs to manually verify the request and 
create a `VacationRequestDecision`.
For traceability, this decision also gets persisted on the database.
Depending on whether the request got approved or not, the `TimeSlot` gets either
persisted or the process ends.

## Design Decisions

- The workflow of the process is inspired by the internal worklog tool of the i3 chair.
**TODO Christoph: Please mention the tool name here.**
- During the creation of the process we tried to incorporate as many different
concepts of the BPMN standard as possible.
- For better readability of the process, we decided to place the validation of 
the input into a sub-process, as depicted in the graphics above.
- To integrate a second actor into the process, we require a timeslot linked
to the 'vacation' project to be approved by `HR` before.
- For faults caused by either user input, e.g., aborting a task, or technical causes
we relied on intermediate throw events to avoid using too many gateways which
do not really contribute to the clarity of the business case. 

## Class Diagram

![CD.svg](img/CD.png)

*Class Diagram 1: Data model of the process*

## Test Cases

### Happy Case: loadsSymbolsFromCD

This test case loads the business process `TimeManagment` via the 
`BPMNConformanceUtils`, and ensures that no errors occur during the processing.

### Bad Case: failsForIncompleteSymbolsFromCD



# Usage of AI Tools

We used Codex for the first translation of our process from the BPMN format to 
the WFM encoding. However, we were required to manually adjust the WFM-file as
we were not completely satisfied with the result.
All other kinds of implementation and writing was done by ourselves without the
assistance of any AI tooling.

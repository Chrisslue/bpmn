# Project Overview

## Goal

# Technical Details

## Project and Gradle Setup

To avoid replicating the Gradle infrastructure of the already existing project
and to simplify the migration into the existing MontiCore codebase later, we
decided to create a fork of the [existing BPMN repository](https://github.com/MontiCore/bpmn) 
instead of working in the created [GitLab repository](https://git.rwth-aachen.de/se-student/ss26/lectures/sle/projects/bpmn4cd).
In addition to the already existing Gradle setup and dependencies, we added 
the `cd2pojo` dependency to be able to process CDs directly.

## Project Structure



# Organizational Details

## Team (alphabetically)

- Johannes Kurth
- Peter Lindner
- Christoph Lütticke
- Abdullah Rehman

**Supervisor**: Sedat Cakici

## Workflow

Most of our work is a result of meeting on Discord on working on the problems at the
same time through pair programming. This allowed us to both keep all members up-to-date 
and also ensure that the time committed per person is spread evenly among the team members.
During the meetings we took notes to guarantee that we meet the expectations of our
supervisor.

# WorkflowManagement Process

![process.svg](img/process.svg)
*Process 1: Main process to handle the business case*
![subprocess.png](img/subprocess.png)
*Process 2: Sub-process to validate the user input*

## Business Case

In our process given above, an `Employee` wants to apply for approval of an upcoming 
vacation.
For this, we have two stakeholders, being the `Employee` and the `HR` representative.
In the beginning the `Employee` opens the dashboard, which shows their (we use
gender-neutral language here) already logged timeslots. These timeslots are 
persisted on a database and locally represented as a list of `TimeSlot` objects. 
Upon choosing an empty timeslot with the intention to mark it as vacation, the 
system creates a new `TimeSlot` object.
**TODO**: Explain `chosenSlot.hasEndTime()` branching.
Next, the `Employee` manually inserts the information of the new `TimeSlot`, and
confirms the input or aborts the creation.
Afterward, the given user input gets validated in the sub-process illustrated above.
In case of invalid inputs, the process shows an error message and redirects the 
`Employee` back to correct the corresponding information.
Otherwise, depending on whether the chosen `project` equals to `vacation` the 
process redirects the vacation request to `HR` or directly persists the created 
`TimeSlot` to the database.
If there occurs an error during persisting the data, an error message gets returned
to the user and the process leads back to user input stage.
In case of a vacation request, `HR` needs to manually verify the request and 
create a `VacationRequestDecision`.
For traceability, this decision also gets persisted on the database.
Depending on whether the request got approved or not, the `TimeSlot` gets either
persisted or the process ends.

## Design Decisions

- During the creation of the process we tried to incorporate as many different
concepts of the BPMN standard as possible.
- For better readability of the process, we decided to place the validation of 
the input into a sub-process, as depicted in the graphics above.
- To integrate a second actor into the process, we require a timeslot linked
to the 'vacation' project to be approved by `HR` before.

## Class Diagrams

![CD.svg](img/CD.png)

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

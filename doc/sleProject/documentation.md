# Project Overview

## Goal

# Technical Details

## Gradle Setup

In addition to the already existing Gradle setup and dependencies, we added 
the `cd2pojo` dependency to be able to process CDs directly.

## Project Structure

# Organizational Details

## Team (alphabetically)

- Johannes Kurth
- Peter Lindner
- Christoph Lütticke
- Abdullah Rehman

## Workflow

Most of our work is a result of meeting on Discord on working on the problems at the
same time. This allowed us to both keep all members up-to-date and also ensure
that the time committed per person is spread evenly among the team members.

# WorkflowManagement Process

![process.svg](img/process.svg)

![subprocess.png](img/subprocess.png)

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


# Business Process Model and Notation (BPMN)
The main purpose of this language is to provide a textual alternative to graphical **BPMN** modeling.

This BPMN language component contains 
* one grammar,
* context conditions,
* pretty printers, and 
* a command-line tool.

## An Example Model

```
package de.monticore.bpmn.examples.vacation;

import de.monticore.bpmn.cds.Vacation.*;

process RequestVacation {

  lane Admin {
    store contract:Contract;
    data report:Report;
    data notice:VacationCardEntry;

    task service DoWork {
      out: report;
    }
    task user Task1 {
      io: {contract, report} -> {report};
    }
    task user Task2 {
      io: {report} -> {notice};
    }
    task Foo; // template:External;
    task service Bar;

    event start -> DoWork -> Task1 -> split xor -> {
      [true] Foo,
      [false] Bar
    } -> merge xor -> event receive timer:[after PT20S] -> Task2 -> event end;
  }

}
```

The following example represents a simplified vacation request process:
* This example defines a Process named `RequestVacation`.
* There is a single lane called `admin`.
* Three data objects are used in this process: `Contract`,`Report` and `VacationCardEntry`.
* Five tasks are specified in this example: `DoWork`, `Task1`, `Task2`, `Foo` and `Bar`.






Further examples can be found here.


## Context Conditions (CoCos)

This sections lists the context conditions for the BPMN language.


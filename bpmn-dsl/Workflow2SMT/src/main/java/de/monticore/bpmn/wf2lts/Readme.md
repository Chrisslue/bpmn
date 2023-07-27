# Transform a BPMN given as workflow ast to a LTS with conditions

## From Ast to LTS

```mermaid
stateDiagram-v2
  direction LR
  s1: Ast
  state "Intermediate Graph With Scopes" as s2
  state "Transformation" as s3{
s31: Graph to LTS
s32: GatewayTransformer
s321: InterleavingStrategy
s33: SubprocessTransfromer
[*] --> s31
s31 --> s32
s32 --> s321
s31 --> s33
}
s4: LTS

[*] --> s1
s1 --> s2: Capture GatewayScope <br> and SubprocessScope
s2 --> s3
s3 --> s4
```

## Overview

- `datastructure` contains necessary auxiliaries as intermediate steps.
- `scopes` contains meta elements for the `IntermediateGraphWithScopes`
- `transformer` are strategy-interfaces and their implementations for transforming the individual components of a bpmn
  to lts
- `collector` are visitors to collect start events or end events of a list of `FlowNodes`

## Current state

| Component                     | Implemented | Tested |
|-------------------------------|-------------|--------|
| GraphBuildingTraverser        | x           | WIP    |
| IntermediateGraph             | x           | -      |
| IntermediateGraphWithScopes   | x           |        |
| LTS                           | x           | WIP    |
| GatewayScope                  | x           | x      |
| SubprocessScope               | x           |        |
| NamingStrategy                | x           | -      |
| GatewayTransformer            | x           | x      |
| SubprocessTransformer         | x           | -      |
| GatewayInterleavingStrategy   | x           | -      |
| Graph2LTSTransformer          | x           | -      |
| DefaultGatewayTransformer     | x           | x      |
| DefaultSubprocessTransformer  | x           | WIP    |
| DefaultGraph2LTSTransformer   | x           | x      |
| DefaultGatewayInterleaving    | x           | -      |
| DefaultParallelInterleaving   | x           | x      |
| DefaultSequentialInterleaving | x           | x      |

#### Problems

- `DefaultSequentialInterleaving` depends on some implicit assumptions like
  - There are no links between the subgraph of each start-transition
  - There is no cycle back to the start node
  - From terminal-state (no outgoing transition) one can take the next path
- `DefaultGatewayTransformer` cant handle a gateway which is on a cycle
  - See `src/test/resources/de/monticore/wf2lts/CyclicGateway.wfm`
- `DefaultSubprocessTrasformer` does not handle boundary events
  - The graph outgoing from the boundary event might be connected to the external graph
  - There is no clear separation between external graph (surrounding the gateway) and internal graph
- `Split -> {A,B}` branching flow construct is not yet supported
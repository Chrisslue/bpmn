# BPMN-DSL Command Line Interface (CLI)

## About

A simple CLI for the BPMN-DSL with support for verifying and exporting BPMN models into the BPMN 2.0 XML exchange format.

![Workflow Tool](./docs/screenshot.png)

### Built With

- [picoli](https://picocli.info) - a mighty tiny command line interface

## Getting Started

### Installation

```bash
mvn clean install
```

This creates the CLI executable `workflow-cli.jar` in target folder `./target`.

## Usage

```text
java -jar workflow-cli.jar -p models/ demo.Example [verify | export]
```

### Global Options

- `-h`, `--help`
- `-p`, `--model-path=DIR` (required)
- `-v`, `--verbose`
- `-V`, `--version`

### Verify Sub-Command

#### Example

```text
java -jar workflow-cli.jar -p models/ demo.Example verify --write-aux
```

#### Options

- `-a`, `--write-aux` - write auxiliary models
- `-h`, `--help`
- `-o`, `--aux-dir=DIR` - output directory, defaults to `./aux`
- `--syntax-only` - perform basic checks, skip structural and behavioral checks
- `-V`, `--version`

### Export Sub-Command

#### Example

```text
java -jar workflow-cli.jar -p models/ demo.Example export
```

#### Options

- `--[no-]check` - skip all checks
- `-h`, `--help`
- `-o`, `--out-dir=DIR` - output directory, defaults to `./out`
- `-V`, `--version`

## Project Structure

```text
.
├── src
|   └── main
|       ├── commands              # verfiy and export commands
|       └── ...
└── target
    └── workflow-cli.jar          # CLI executable
```

## Contact

Erik Müller - erik.mueller@rwth-aachen.de

[picoli-website]: https://picocli.info

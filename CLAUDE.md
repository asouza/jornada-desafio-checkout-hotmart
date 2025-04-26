# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands
- Build: `./mvnw clean install`
- Run: `./mvnw spring-boot:run`
- Test: `./mvnw test`
- Single test: `./mvnw test -Dtest=TestClassName#methodName`
- Test a specific class: `./mvnw test -Dtest=ClassName*`

## Code Style Guidelines
- Indentation: 4 spaces
- Line endings: LF
- Encoding: UTF-8
- Java version: 21
- Imports: Organize imports alphabetically, no wildcards
- Naming: CamelCase for classes/methods, constants in UPPERCASE_WITH_UNDERSCORES
- Tests: Use JUnit 5 with descriptive @DisplayName annotations
- Assertions: Use JUnit's Assertions class with expected value first
- Error handling: Use specific exceptions and document with comments
- Bean validation: Use @Valid and validation constraints on DTOs
- Documentation: Document public methods and non-obvious logic
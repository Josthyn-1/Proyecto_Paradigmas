# Upgrade Plan: Agenda (20261112-001)

- **Generated**: 2026-11-12T14:20:00-05:00
- **HEAD Branch**: N/A
- **HEAD Commit ID**: N/A

## Available Tools

**JDKs**
- JDK 17.0.21: C:\Program Files\Eclipse Adoptium\jdk-17.0.21.9-hotspot (current project JDK, used by baseline validation)
- JDK 25.0.5: C:\Users\feard\.jdks\jdk-25.0.5+8 (required by step 3)

**Build Tools**
- Maven 3.9.11: C:\Users\feard\.maven\apache-maven-3.9.11\bin\mvn (used for compile/test validation)
- Maven Wrapper: not present in this project

## Guidelines

- Preserve unrelated work and keep the requested Java 25 target unchanged.
- Keep build/test commands focused on the existing Maven project structure.
- Do not pause for approval; execute the upgrade and validate with the current Maven test suite.
- Version control tooling is unavailable in this environment, so the working branch and commit metadata are recorded as N/A.

> Note: You can add any specific guidelines or constraints for the upgrade process here if needed, bullet points are preferred.

## Options

- Working branch: appmod/java-upgrade-20261112-001
- Run tests before and after the upgrade: true

## Upgrade Goals

- Java 25

## Technology Stack

| Technology/Dependency | Current | Min Compatible Version | Why Incompatible |
| ---------------------- | ------- | ---------------------- | --------------- |
| Java | 11 | 25 | User requested |
| Maven | 3.9.11 | 3.9.11 | Compatible with Java 25 and installed for this upgrade |
| JUnit Jupiter | 5.10.2 | 5.10.2 | Compatible with Java 25; no change required |
| maven-surefire-plugin | 3.2.5 | 3.2.5 | Current version supports Java 17+ and works with Java 25 |

## Derived Upgrades

- Java 25 requires the project build to target Java 25 and run under the JDK 25 toolchain.
- Maven 3.9.x is the recommended baseline for a Java 25 build and is already installed for execution.
- No Spring or other framework upgrade is required because the project is a plain Java Maven application and the direct dependencies are already compatible with Java 25.

## Impact Analysis

### Subsection: Dependency Changes

| File | Dependency | Current | Action | Target | Reason |
|------|-----------|---------|--------|--------|--------|
| pom.xml | maven.compiler.source | 11 | upgrade | 25 | User requested Java 25 target |
| pom.xml | maven.compiler.target | 11 | upgrade | 25 | User requested Java 25 target |
| pom.xml | maven.compiler.release | not set | add | 25 | Ensures the JDK 25 toolchain is used consistently |
| pom.xml | maven-compiler-plugin | not pinned | add | 3.14.0+ | Recommended explicit compiler plugin for modern JDK compatibility |

### Subsection: Source Code Changes

| File | Location | Current | Required Change | Reason |
|------|----------|---------|----------------|--------|
| None identified | - | - | No source-level migration required for Java 25 in this project | The project is a plain Java application with no removed JDK APIs or framework migrations |

### Subsection: Configuration Changes

| File | Property/Setting | Current | Required Change | Reason |
|------|-----------------|---------|----------------|--------|
| pom.xml | compiler source/target | 11 | Set to 25 | Required by user goal |

### Subsection: CI/CD Changes

| File | Location | Current | Required Change |
|------|----------|---------|----------------|
| None identified | - | - | No pipeline files referenced in the project |

### Subsection: Risks & Warnings

- **Java 25 toolchain compatibility**: The project currently compiles under Java 11; the build needs to be verified under Java 25 because any compiler or test incompatibility would only surface with the newer JDK. **Mitigation**: run Maven compile and full test suite under JDK 25 and fix any breakage surfaced by the toolchain.
- **Dependency compatibility drift**: No framework-level migration is required, but a modern toolchain still needs explicit compiler settings to avoid accidentally compiling against the wrong Java level. **Mitigation**: set `maven.compiler.release` and version the compiler plugin explicitly.

## Upgrade Steps

- Step 1: Setup Environment
  - **Rationale**: The project must be built and validated on JDK 25; the required toolchain and build tool need to exist before verification.
  - **Changes to Make**: Ensure JDK 25 and Maven 3.9.11 are available and recorded in the environment.
  - **Verification**: `java -version` and `mvn -version`; expected JDK 25 and Maven 3.9.11.

- Step 2: Setup Baseline
  - **Rationale**: Validate the current project state before modifying the Java level so the final validation can be compared against known-good behavior.
  - **Changes to Make**: Run the existing Maven suite under the current JDK 17 before migrating to Java 25.
  - **Verification**: `mvn clean test -q`; expected success and a known-good baseline.

- Step 3: Upgrade Build to Java 25
  - **Rationale**: The project is a simple Maven application; the minimal required change is to update the compiler configuration to Java 25.
  - **Changes to Make**: Apply all dependency changes from the Impact Analysis and update `pom.xml` to compile with Java 25 using the JDK 25 toolchain.
  - **Verification**: `JAVA_HOME=.../jdk-25.0.5+8 mvn clean test-compile -q`; expected compilation success.

- Step 4: Final Validation
  - **Rationale**: The success criteria require the complete test suite to pass under Java 25. Any failing test must be fixed before completion.
  - **Changes to Make**: Run the full `mvn clean test` suite under JDK 25 and fix any regressions.
  - **Verification**: `JAVA_HOME=.../jdk-25.0.5+8 mvn clean test -q`; expected 100% pass rate.


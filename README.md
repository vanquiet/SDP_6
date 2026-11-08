# Assignment 6

Implementation of an in-memory hierarchical file system using the Visitor design pattern and Double Dispatch mechanism in Java 17 and Maven.

## System Components

### 1. Model (`model/`)
- `FileSystemElement`: Common interface declaring `accept(FileSystemVisitor visitor)`.
- `FileNode`: Represents a leaf file with name and non-negative byte size.
- `DirectoryNode`: Represents a folder managing child elements while preventing duplicate sibling names, cycles, and multi-parent ownership.

### 2. Operations / Visitors (`visitor/`)
- `TotalSizeVisitor`: Computes the accumulated byte size of all files across nested folders.
- `SearchVisitor`: Filters files matching a substring name query and minimum size threshold (`size >= minSize`), returning deterministic root-relative paths.
- `StatisticsVisitor`: Counts total files, directories, and calculates the maximum tree depth.

---

## Prerequisites
- Java 17 (JDK)
- Apache Maven 3.8+

---

## Build & Run Instructions

### 1. Run Automated Tests
To execute all JUnit 5 test cases (T1–T8):
```bash
mvn clean test
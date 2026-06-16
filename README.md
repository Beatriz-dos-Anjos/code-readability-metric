# Code Readability Metric — Java Code Readability Analyzer

A Java code analyzer that measures readability through 5 metrics (F1 to F5), based on the Buse & Weimer model. The tool parses Java source files into an Abstract Syntax Tree (AST), applies each metric visitor, and produces a ranked report with per-file and per-feature scores.

## 1. Prerequisites

Before running the project, make sure you have the following installed:

### 1. **Java Development Kit (JDK) 17 or higher**
   - **Download:** https://www.oracle.com/java/technologies/downloads/
   - **Verify installation:**
     ```bash
     java -version
     # Should show version 17 or higher
     ```

### 2. **Apache Maven 3.8 or higher**
   - **Download:** https://maven.apache.org/download.cgi
   - **Installation (Linux/Mac):**
     ```bash
     tar -xzf apache-maven-3.x.x-bin.tar.gz
     export PATH=$PATH:/path/to/apache-maven-3.x.x/bin
     ```
   - **Verify installation:**
     ```bash
     mvn -version
     # Should show version 3.8 or higher
     ```

---

## 2. Installation and Build

### 1. Clone or download the project
```bash
git clone <repository-URL>
cd code-readability-metric
```

### 2. Build the project
```bash
mvn clean package
```

This generates `target/legibilidade.jar` — an executable fat JAR with all dependencies bundled in.

---

## 3. Usage

### Analyze a single file
```bash
java -jar target/legibilidade.jar samples/F1Good.java
```

### Analyze a directory (recursive)
```bash
java -jar target/legibilidade.jar /path/to/project/src
```

### Example output
```
Found 4 Java files to analyze

[1/4] Analyzing: F1Bad.java
[2/4] Analyzing: F1Good.java
[3/4] Analyzing: F3Bad.java
[4/4] Analyzing: F3Good.java

=== ANALYSIS SUMMARY ===

Ranking of archives by final score (worst to best):
Archive                                                      | Score
-------------------------------------------------------------------------
samples/F1Bad.java                                           | 50.00
samples/F3Bad.java                                           | 84.38
samples/F1Good.java                                          | 100.00
samples/F3Good.java                                          | 100.00

Repository's average score: 83.59

Score's Distribution:
90-100:       2
70-89:        1
50-69:        1
Below 50:     0
=========================
```

### JSON Report
In addition to the console summary, the analyzer generates a `report.json` file in the current directory with full details for each file and each feature (F1 to F5).

---

## 4. How to Test the Tool

### 1. Test the Final Product (Real Analysis)
Build the executable and run the project against a folder:

```bash
# Package the tool (generates the .jar file)
mvn package

# Run the analysis on the desired folder
java -jar target/legibilidade.jar samples/
```

This performs the full processing pipeline and displays the **Analysis Summary** with the readability ranking.

### 2. Test the Source Code (Automated Tests)
Run Maven's test suite to verify the scoring logic for all 5 features:

```bash
mvn test
```

Unit tests are located in `src/test/java/readability/visitors/` (one test class per feature: F1Test through F5Test) and `src/test/java/readability/report/ReportWriterTest.java`.

---

## 5. File Structure

### **Configuration and Build**

#### `pom.xml`
Defines project dependencies and Maven configuration:
- **JavaParser 3.25.10**: Parses Java source code into an AST
- **Gson 2.10.1**: Serializes reports to JSON
- **JUnit 5**: Testing framework
- **Maven Shade Plugin**: Creates an executable fat JAR

**Main class defined:** `readability.Main`

---

### **Main Source Code**

#### `src/main/java/readability/Main.java`
**Application entry point.**

- Accepts a path as an argument (a `.java` file or a directory)
- If a single file: analyzes only that file
- If a directory: recursively finds all `.java` files
- Displays real-time progress: `[X/Y] file.java`
- Delegates to `FileAnalyzer`, `ScoreCalculator`, and `ReportWriter`

---

#### `src/main/java/readability/analyzer/FileAnalyzer.java`
**Orchestrates the analysis of a single file.**

- Parses the file with `StaticJavaParser.parse()`
- Runs all registered visitors (F1–F5) via `VisitorRegistry`
- Returns a complete `FileReport`
- On parse error: logs `[SKIP] file.java` and marks `parseable=false`

---

#### `src/main/java/readability/score/ScoreCalculator.java`
**Calculates the final readability score for a file.**

- Applies the Buse & Weimer formula: `score = 100 × (1 − violations / opportunities)`
- If `opportunities == 0`, the score defaults to 100
- The `finalScore` is the arithmetic mean of all 5 feature scores

---

#### `src/main/java/readability/report/ReportWriter.java`
**Produces analysis output.**

- Writes a pretty-printed JSON file (`report.json`) with all file and feature details
- Prints the console summary: ranked table, average score, and score distribution

---

### **Data Models**

#### `src/main/java/readability/model/FeatureResult.java`
**Result of analyzing a single feature.**

| Field | Description |
|-------|-------------|
| `int violations` | Number of violations found |
| `int opportunities` | Total opportunities where the metric applies |
| `double score` | Calculated percentage (0–100) |
| `int featureId` | Which feature (1–5) produced this result |

Score is automatically clamped to [0, 100].

---

#### `src/main/java/readability/model/FileReport.java`
**Complete analysis report for a single file.**

| Field | Description |
|-------|-------------|
| `String filePath` | Path to the analyzed file |
| `List<FeatureResult> features` | Results for F1–F5 |
| `boolean parseable` | Whether the file was parsed successfully |
| `double finalScore` | Arithmetic mean of all feature scores |

---

### **Visitors (Metric Analysis)**

#### `src/main/java/readability/visitors/FeatureVisitor.java`
**Common interface for all visitors.**

Every visitor must implement:
- `FeatureResult analyze(CompilationUnit ast)`: runs the metric on the AST
- `int getFeatureId()`: returns 1–5
- `String getFeatureName()`: human-readable name (e.g., `"Hidden Braces (F1)"`)

---

#### `src/main/java/readability/visitors/F1HiddenBracesVisitor.java`
**Feature 1: Missing Braces**

Detects control flow structures (`if`, `for`, `foreach`, `while`) that omit curly braces `{}`.

| Structure | Opportunities counted |
|-----------|-----------------------|
| `if` | +1 for then-branch, +1 if `else` present (except `else if` chains) |
| `for` / `foreach` / `while` | +1 per loop |

**Violation:** body is not a `BlockStmt` (no braces).

```java
// Good (score 100%)
if (x > 5) {
    System.out.println("OK");
}

// Bad (violation)
if (x > 5)
    System.out.println("OK");
```

---

#### `src/main/java/readability/visitors/F2EmbeddedAssignVisitor.java`
**Feature 2: Embedded Assignments**

Detects assignments (`=`) and increment/decrement operators (`++`/`--`) embedded inside the conditional expressions of `if`, `while`, and `for` statements.

| Structure | Opportunity |
|-----------|-------------|
| `if` / `while` | +1 per conditional |
| `for` | +1 if a compare expression is present |

**Violation:** an `AssignExpr` or increment/decrement `UnaryExpr` is found inside the condition.

```java
// Good (score 100%)
int x = getValue();
if (x > 0) { ... }

// Bad (violation)
if ((x = getValue()) > 0) { ... }
```

---

#### `src/main/java/readability/visitors/F3OperatorsPerLineVisitor.java`
**Feature 3: Operators Per Line**

Counts binary and unary operator occurrences per source line. Any line with **3 or more operators** is a violation.

- **Opportunity:** every source line that contains at least one operator
- **Violation:** line with ≥ 3 operators

```java
// Good (score 100%)
boolean valid = age >= 18 && active;

// Bad (violation — 4 operators on one line)
boolean valid = a > 0 && b < 10 && c != 0 || d == 1;
```

---

#### `src/main/java/readability/visitors/F4NestingDepthVisitor.java`
**Feature 4: Nesting Depth**

For each method, computes the maximum control-flow nesting depth. Methods with a max depth **greater than 2** are considered violations.

- **Opportunity:** every method declaration
- **Violation:** method whose max depth exceeds 2
- Counted structures: `if`, `for`, `foreach`, `while`, `do`, `switch`

```java
// Good (depth 2, score 100%)
void process() {
    for (int i = 0; i < n; i++) {  // depth 2
        doWork(i);
    }
}

// Bad (depth 3, violation)
void process() {
    for (int i = 0; i < n; i++) {   // depth 2
        if (condition) {             // depth 3 — violation
            doWork(i);
        }
    }
}
```

---

#### `src/main/java/readability/visitors/F5ParameterCountVisitor.java`
**Feature 5: Parameter Count**

Detects methods and constructors with **more than 3 parameters**, which hurts readability and testability.

- **Opportunity:** every method or constructor declaration
- **Violation:** more than 3 parameters

```java
// Good (score 100%)
void save(User user, Database db) { ... }

// Bad (violation — 4 parameters)
void save(String name, String email, int age, String role) { ... }
```

---

#### `src/main/java/readability/visitors/VisitorRegistry.java`
**Central registry for all visitors.**

- Maintains the list of all 5 visitors
- Calls `analyze()` on each for a given AST
- Returns an ordered list of `FeatureResult` objects

---

### **Sample Files**

| File | Description | Expected F-Score |
|------|-------------|-----------------|
| `samples/F1Good.java` | All control structures use braces | F1 = 100% |
| `samples/F1Bad.java` | Nearly all control structures lack braces | F1 ≈ 0% |
| `samples/F2Good.java` | No embedded assignments in conditions | F2 = 100% |
| `samples/F2Bad.java` | Assignments and `++`/`--` inside conditions | F2 ≈ 0% |
| `samples/F3Good.java` | All lines have fewer than 3 operators | F3 = 100% |
| `samples/F3Bad.java` | Multiple lines with ≥ 3 operators | F3 ≈ 0% |
| `samples/F4Good.java` | All methods have nesting depth ≤ 2 | F4 = 100% |
| `samples/F4Bad.java` | Methods with nesting depth > 2 | F4 ≈ 0% |
| `samples/F5Good.java` | All methods have ≤ 3 parameters | F5 = 100% |
| `samples/F5Bad.java` | Methods with more than 3 parameters | F5 ≈ 0% |
| `samples/real/` | Real-world Java files for empirical validation | Mixed |

---

## 6. Execution Flow

```
1. User runs: java -jar legibilidade.jar <path>
                       ↓
2. Main.java
   - Discovers .java files (single file or recursive)
   - Displays [X/Y] for each file
                       ↓
3. FileAnalyzer.analyze(path)
   - Parses with StaticJavaParser
   - Calls VisitorRegistry.analyzeFile(ast)
                       ↓
4. VisitorRegistry.analyzeFile(ast)
   - Calls visitor.analyze(ast) for each of the 5 registered visitors
   - Collects FeatureResult from each
                       ↓
5. Visitors F1–F5 each:
   - Traverse the AST
   - Count violations and opportunities
   - Return FeatureResult with score
                       ↓
6. ScoreCalculator.calculate(report)
   - Computes per-feature scores
   - Sets finalScore = arithmetic mean of F1–F5
                       ↓
7. ReportWriter.writeReports(reports, outputPath)
   - Writes report.json
   - Prints console summary (ranking, average, distribution)
```

---

## 7. Project Phases

### **Phase 1 — Foundation (Person 1)**
- Maven setup (`pom.xml`)
- Models (`FeatureResult`, `FileReport`)
- `Main.java`
- `FileAnalyzer.java`
- `F1HiddenBracesVisitor.java`
- Samples (`F1Good.java`, `F1Bad.java`)

### **Phase 2 — Parallel Development (Persons 2, 3, 4, 5)**
- Person 2: F2 + ScoreCalculator
- Person 3: F3 + ReportWriter
- Person 4: F4 + JUnit tests
- Person 5: F5 + Empirical validation

### **Phase 3 — Integration (Person 1)**
- Pulls all visitors into `FileAnalyzer`
- Smoke test with samples
- Resolves any score discrepancies

### **Phase 4 — Validation (Person 5)**
- Runs the tool on external repositories
- Collects survey data
- Documents results

---

## 8. Local Development

### Compile and run tests
```bash
mvn clean test
```

### Compile only
```bash
mvn compile
```

### Clean compiled files
```bash
mvn clean
```

### Generate fat JAR
```bash
mvn package
```

---

## 9. Score Interpretation

Each feature produces a score from 0% to 100%. The `finalScore` is the arithmetic mean of all five.

| Score Range | Interpretation | Recommended Action |
|-------------|----------------|--------------------|
| 90–100% | Excellent | Maintain the standard |
| 70–89% | Good | Review remaining violations |
| 50–69% | Fair | Refactor to fix violations |
| 20–49% | Poor | Urgent refactoring needed |
| 0–19% | Very poor | Complete refactoring required |

---

## 10. License

This project is licensed under the MIT License.

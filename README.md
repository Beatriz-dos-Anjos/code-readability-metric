# Code Readability Metric - Java Code Readability Analyzer

A Java code analyzer that measures readability through 5 different metrics (F1 to F5). This project was developed in collaborative phases, starting with Feature 1 (F1), which detects missing braces in control flow structures.

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
     # Extract the archive
     tar -xzf apache-maven-3.x.x-bin.tar.gz
     # Add to PATH
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

Top 10 worst files by final score:
File                                                         | Score
-------------------------------------------------------------------------
samples\F1Bad.java                                           | 50.00
samples\F3Bad.java                                           | 84.38
samples\F1Good.java                                          | 100.00
samples\F3Good.java                                          | 100.00

Repository average score: 83.59

Score distribution:
90-100:    2
70-89:     1
50-69:     1
Below 50:  0
=========================
```

### JSON Report
In addition to the console summary, the analyzer generates a `report.json` file in the current directory with full details for each file and each feature (F1 to F5).

---

## 4. How to Test the Tool

There are two approaches to "testing" this project, depending on your goal:

### 1. Test the Final Product (Real Analysis)
If you want to see the tool in action — computing metrics for a code folder and printing the final ranking table (e.g., evaluating the `samples` folder) — build the executable and run the project:

```bash
# 1. Package the tool (generates the .jar file)
mvn package

# 2. Run the analysis on the desired folder
java -jar target/legibilidade.jar samples/
```

This performs the full processing pipeline and displays the **Analysis Summary** with the readability ranking on your screen.

### 2. Test the Source Code (Automated Tests)
If you are a developer who modified internal metric logic and want to ensure the scoring math or bug detection still works correctly, use Maven's test suite:

```bash
mvn test
```

This command does **not** print a ranking table. It silently runs validations (Unit Tests located in `src/test/...`) using the files in the `samples` folder to confirm that the tool's scoring system remains reliable and error-free.

---

## 5. File Structure

### **Configuration and Build**

#### `pom.xml`
Defines project dependencies and Maven configuration:
- **JavaParser 3.25.10**: Library for parsing Java code into an AST (Abstract Syntax Tree)
- **Gson 2.10.1**: Data serialization to JSON
- **JUnit 5**: Testing framework
- **Maven Shade Plugin**: Creates an executable fat JAR with all dependencies

**Main class defined:** `readability.Main`

---

### **Main Source Code**

#### `src/main/java/readability/Main.java`
**Application entry point.**

**What it does:**
- Accepts a path as an argument (a `.java` file or a directory)
- If a single file: analyzes only that file
- If a directory: recursively walks it and finds all `.java` files
- Displays real-time progress: `[X/Y] file.java`
- Handles errors gracefully without interrupting the analysis
- Prints a final summary with the score of each file

**Example usage:**
```bash
java -jar legibilidade.jar /my/project
```

---

#### `src/main/java/readability/analyzer/FileAnalyzer.java`
**Orchestrates the analysis of a single file.**

**What it does:**
- Receives a `Path` pointing to a `.java` file
- Uses `StaticJavaParser.parse(file)` to convert the file into an AST
- Runs all registered visitors (F1, F2, F3, etc.)
- Collects the `FeatureResult` from each visitor
- Returns a complete `FileReport`
- On parse error: logs `[SKIP] file.java` and returns a report with `parseable=false`

**Responsibilities:**
- Safe parsing
- Visitor integration
- Exception handling

---

### **Data Models**

#### `src/main/java/readability/model/FeatureResult.java`
**Result of analyzing a single feature.**

**Fields:**
- `int violations`: Number of violations found
- `int opportunities`: Total number of opportunities where the metric applies
- `double score`: Calculated percentage (0–100%)
- `int featureId`: Which feature (1–5) produced this result

**Characteristics:**
- Immutable and thread-safe
- Score is automatically clamped to [0, 100]
- Overloaded constructors for compatibility

**Example:**
```java
new FeatureResult(3, 10, 70.0, 1)
// 3 violations out of 10 opportunities = 70% score
```

---

#### `src/main/java/readability/model/FileReport.java`
**Complete analysis report for a single file.**

**Fields:**
- `String filePath`: Path to the analyzed file
- `List<FeatureResult> features`: List of exactly 5 features (F1 to F5)
- `boolean parseable`: Whether the file was parsed successfully
- `double finalScore`: Consolidated final score (calculated by ScoreCalculator in Phase 2)

**Characteristics:**
- Tracks files that failed to parse
- Groups all results for a file
- Supports adding the final score after the fact

---

### **Visitors (Metric Analysis)**

#### `src/main/java/readability/visitors/FeatureVisitor.java`
**Common interface for all visitors.**

**Methods every visitor must implement:**
- `FeatureResult analyze(CompilationUnit ast)`: Analyzes the AST and returns a result
- `int getFeatureId()`: Returns 1–5 identifying which feature this is
- `String getFeatureName()`: Human-readable feature name (e.g., `"Hidden Braces (F1)"`)

**Purpose:**
- Enforce a consistent interface
- Enable dynamic visitor registration
- Simplify integration in Phase 3

---

#### `src/main/java/readability/visitors/F1HiddenBracesVisitor.java`
**Feature 1: Missing Braces Detection**

**What it measures:**
- Control flow structures (`if`, `for`, `while`, `foreach`) that omit curly braces `{}`
- Braces are required for good readability and to avoid subtle bugs

**Counting logic:**
- **If Statement:** +1 opportunity for the then-branch, +1 if there is an else (except else-if chains)
- **For Loop:** +1 opportunity per loop
- **ForEach Loop:** +1 opportunity per loop
- **While Loop:** +1 opportunity per loop
- **Violation:** Counted when the body is not a `BlockStmt` (i.e., has no braces)

**Score formula:**
```
If opportunities == 0: score = 100% (no control flow structures present)
Otherwise:             score = 100 × (1 - violations / opportunities)
```

**Examples:**

**Good (100% F1):**
```java
if (x > 5) {
    System.out.println("OK");
}
```

❌ **Bad (0% F1):**
```java
if (x > 5)
    System.out.println("OK");
```

---

#### `src/main/java/readability/visitors/VisitorRegistry.java`
**Central registry for all visitors.**

**What it does:**
- Maintains a static list of all visitors (F1 to F5)
- Calls `analyze()` on each visitor for a given AST
- Returns an ordered list of results
- Allows dynamic registration via `registerVisitor()`

**Phase 1:** Only F1 registered
**Phase 2:** Teammates register F2, F3, F4, F5
**Phase 3:** All 5 visitors integrated and validated

---

### **Sample Files for Testing**

#### `samples/F1Good.java`
**Example of well-structured code — Expected score: 100%**

**Characteristics:**
- All `if`, `for`, and `while` statements use braces `{}`
- 3 methods with real logic:
  - `validateEmail()`: String validation
  - `countValidUsers()`: Loop with a condition
  - `processData()`: Nested loops with else-if
- Readable, unambiguous code

**Result:** F1 = 100%

---

#### `samples/F1Bad.java`
**Example of problematic code — Expected score: near 0%**

**Characteristics:**
- Nearly all control structures omit braces
- **Same logic** as F1Good but written in a poor style
- Demonstrates the dangling-else risk
- Hard to read and maintain

**Result:** F1 ≈ 0%

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
   - Calls visitor.analyze(ast) for each registered visitor
   - Collects FeatureResult from each
                           ↓
5. F1HiddenBracesVisitor.analyze(ast)
   - Visits IfStmt, ForStmt, ForEachStmt, WhileStmt
   - Counts violations and opportunities
   - Calculates score
   - Returns FeatureResult
                           ↓
6. Main.java
   - Prints final summary with scores
   - Calculates averages
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

| F1 Score | Interpretation | Recommended Action |
|----------|----------------|--------------------|
| 100% | Excellent — All braces present | Maintain the standard |
| 80–99% | Good — Very few omissions | Review the missing spots |
| 50–79% | Fair — Many omissions | Refactor to add braces |
| 20–49% | Poor — Most blocks lack braces | Urgent refactoring needed |
| 0–19% | Very poor — Almost no braces | Complete refactoring required |

---

## 10. License

This project is licensed under the MIT License.

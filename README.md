# Code Readability Metric - Analisador de Legibilidade de Código Java

Um analisador de código Java que mede a legibilidade através de 5 métricas diferentes (F1 a F5). Este projeto foi desenvolvido em fases colaborativas, começando com a Feature 1 (F1) que detecta omissão de chaves em estruturas de controle.

## 📋 Pré-requisitos

Antes de executar o projeto, você precisa ter instalado:

### 1. **Java Development Kit (JDK) 17 ou superior**
   - **Download:** https://www.oracle.com/java/technologies/downloads/
   - **Verificar instalação:**
     ```bash
     java -version
     # Deve mostrar versão 17 ou superior
     ```

### 2. **Apache Maven 3.8 ou superior**
   - **Download:** https://maven.apache.org/download.cgi
   - **Instalação (Linux/Mac):**
     ```bash
     # Extrair o arquivo
     tar -xzf apache-maven-3.x.x-bin.tar.gz
     # Adicionar ao PATH
     export PATH=$PATH:/caminho/para/apache-maven-3.x.x/bin
     ```
   - **Verificar instalação:**
     ```bash
     mvn -version
     # Deve mostrar versão 3.8 ou superior
     ```

---

## 🚀 Instalação e Compilação

### 1. Clonar ou baixar o projeto
```bash
git clone <URL-do-repositorio>
cd code-readability-metric
```

### 2. Compilar o projeto
```bash
mvn clean package
```

Isso gera o arquivo `target/legibilidade.jar` - um  jar executável com todas as dependências incluídas.

---

## 🎯 Como Usar

### Analisar um arquivo único
```bash
java -jar target/legibilidade.jar samples/F1Good.java
```

### Analisar um diretório (recursivo)
```bash
java -jar target/legibilidade.jar /caminho/para/projeto/src
```

### Exemplo de saída
```
Found 2 Java files to analyze

[1/2] Analyzing: F1Good.java
[2/2] Analyzing: F1Bad.java

=== Analysis Summary ===

File Scores:
  samples/F1Good.java                                F1: 100,00%
  samples/F1Bad.java                                 F1:   0,00%

Average F1 Score: 50,00%
```

---

## 📁 Estrutura de Arquivos

### **Configuração e Construção**

#### `pom.xml`
Define as dependências do projeto e configuração do Maven:
- **JavaParser 3.25.10**: Biblioteca para análise de código Java em AST (Abstract Syntax Tree)
- **Gson 2.10.1**: Serialização de dados para JSON
- **JUnit 5**: Framework de testes
- **Maven Shade Plugin**: Cria um fat jar executável com todas as dependências

**Classe principal definida:** `readability.Main`

---

### **Código Principal**

#### `src/main/java/readability/Main.java`
**Ponto de entrada da aplicação.**

**O que faz:**
- Recebe um caminho como argumento (arquivo `.java` ou diretório)
- Se for arquivo único: analisa apenas ele
- Se for diretório: percorre recursivamente encontrando todos os `.java`
- Exibe progresso em tempo real: `[X/Y] arquivo.java`
- Trata erros graciosamente sem interromper a análise
- Imprime resumo final com scores de cada arquivo

**Exemplo de uso:**
```bash
java -jar legibilidade.jar /meu/projeto
```

---

#### `src/main/java/readability/analyzer/FileAnalyzer.java`
**Orquestrador de análise de um arquivo.**

**O que faz:**
- Recebe um `Path` para um arquivo `.java`
- Usa `StaticJavaParser.parse(file)` para converter o arquivo em AST (Abstract Syntax Tree)
- Executa todos os visitors registrados (F1, F2, F3, etc)
- Coleta os `FeatureResult` de cada visitor
- Retorna um `FileReport` completo
- Em caso de erro de parse: loga `[SKIP] arquivo.java` e retorna relatório com `parseable=false`

**Responsabilidades:**
- Parsing seguro
- Integração de visitors
- Tratamento de exceções

---

### **Modelos de Dados**

#### `src/main/java/readability/model/FeatureResult.java`
**Resultado da análise de uma single feature.**

**Campos:**
- `int violations`: Número de violações encontradas
- `int opportunities`: Número total de oportunidades onde a métrica se aplica
- `double score`: Percentual calculado (0-100%)
- `int featureId`: Qual feature (1-5) gerou este resultado

**Características:**
- Imutável e thread-safe
- Score automaticamente clamped entre [0, 100]
- Construtores sobrecarregados para compatibilidade

**Exemplo:**
```java
new FeatureResult(3, 10, 70.0, 1)
// 3 violações em 10 oportunidades = score 70%
```

---

#### `src/main/java/readability/model/FileReport.java`
**Relatório completo de análise de um arquivo.**

**Campos:**
- `String filePath`: Caminho do arquivo analisado
- `List<FeatureResult> features`: Lista com exatamente 5 features (F1 a F5)
- `boolean parseable`: Se o arquivo foi parseado com sucesso
- `double finalScore`: Score final consolidado (calculado pelo ScoreCalculator na Phase 2)

**Características:**
- Permite rastreamento de arquivos que falharam no parse
- Agrupa todos os resultados de um arquivo
- Suporta adição posterior do score final

---

### **Visitors (Análise de Métricas)**

#### `src/main/java/readability/visitors/FeatureVisitor.java`
**Interface comum para todos os visitors.**

**Métodos que todo visitor deve implementar:**
- `FeatureResult analyze(CompilationUnit ast)`: Analisa o AST e retorna resultado
- `int getFeatureId()`: Retorna 1-5 identificando qual feature
- `String getFeatureName()`: Nome legível da feature (ex: "Hidden Braces (F1)")

**Propósito:**
- Garantir interface consistente
- Permitir registro dinâmico de visitors
- Facilitar integração no Phase 3

---

#### `src/main/java/readability/visitors/F1HiddenBracesVisitor.java`
**Feature 1: Detecção de Chaves Omitidas**

**O que mede:**
- Estruturas de controle (`if`, `for`, `while`, `foreach`) que não usam chaves `{}`
- Chaves são obrigatórias para boa legibilidade e evitar erros

**Lógica de contagem:**
- **If Statement:** +1 oportunidade se tem then, +1 se tem else (exceto else-if)
- **For Loop:** +1 oportunidade por loop
- **ForEach Loop:** +1 oportunidade por loop
- **While Loop:** +1 oportunidade por loop

- **Violação:** Contada quando o corpo não é `BlockStmt` (não tem chaves)

**Fórmula de score:**
```
Se opportunities == 0: score = 100% (nenhuma estrutura de controle)
Senão: score = 100 × (1 - violations/opportunities)
```

**Exemplos:**

✅ **Bom (100% F1):**
```java
if (x > 5) {
    System.out.println("OK");
}
```

❌ **Ruim (0% F1):**
```java
if (x > 5)
    System.out.println("OK");
```

---

#### `src/main/java/readability/visitors/VisitorRegistry.java`
**Registro central de todos os visitors.**

**O que faz:**
- Mantém lista estática de todos os visitors (F1 a F5)
- Executa `analyze()` em cada visitor para um AST dado
- Retorna lista ordenada de resultados
- Permite registro dinâmico via `registerVisitor()`

**Phase 1:** Apenas F1 registrado
**Phase 2:** Colegas registram F2, F3, F4, F5
**Phase 3:** Todos os 5 visitors integrados e validados

---

### **Samples para Teste**

#### `samples/F1Good.java`
**Exemplo de código bem estruturado - Score esperado: 100%**

**Características:**
- Todos os `if`, `for`, `while` usam chaves `{}`
- 3 métodos com lógica real:
  - `validateEmail()`: Validação de string
  - `countValidUsers()`: Loop com condição
  - `processData()`: Loops aninhados com else-if
- Código legível e sem ambiguidades

**Resultado:** F1 = 100%

---

#### `samples/F1Bad.java`
**Exemplo de código com problemas - Score esperado: próximo de 0%**

**Características:**
- Praticamente todas as estruturas omitem chaves
- **Mesma lógica** do F1Good mas com estilo ruim
- Demonstra risco de dangling else
- Código difícil de ler e manter

**Resultado:** F1 ≈ 0%

---

## 🔄 Fluxo de Execução

```
1. User executa: java -jar legibilidade.jar <path>
                           ↓
2. Main.java
   - Descobre arquivos .java (único ou recursivo)
   - Exibe [X/Y] para cada arquivo
                           ↓
3. FileAnalyzer.analyze(path)
   - Parse com StaticJavaParser
   - Chama VisitorRegistry.analyzeFile(ast)
                           ↓
4. VisitorRegistry.analyzeFile(ast)
   - Executa visitor.analyze(ast) para cada visitor registrado
   - Coleta FeatureResult de cada um
                           ↓
5. F1HiddenBracesVisitor.analyze(ast)
   - Visita IfStmt, ForStmt, ForEachStmt, WhileStmt
   - Conta violations e opportunities
   - Calcula score
   - Retorna FeatureResult
                           ↓
6. Main.java
   - Imprime resumo final com scores
   - Calcula médias
```

---

## 🏗️ Fases do Projeto

### **Phase 1 - Base (Pessoa 1)**
- ✅ Configuração Maven (pom.xml)
- ✅ Modelos (FeatureResult, FileReport)
- ✅ Main.java
- ✅ FileAnalyzer.java
- ✅ F1HiddenBracesVisitor.java
- ✅ Samples (F1Good.java, F1Bad.java)

### **Phase 2 - Paralelo (Pessoas 2, 3, 4, 5)**
- Pessoa 2: F2 + ScoreCalculator
- Pessoa 3: F3 + ReportWriter
- Pessoa 4: F4 + JUnit tests
- Pessoa 5: F5 + Validação empírica

### **Phase 3 - Integração (Pessoa 1)**
- Puxa todos os visitors para FileAnalyzer
- Smoke test com samples
- Resolve divergências de score

### **Phase 4 - Validação (Pessoa 5)**
- Executa em repositórios externos
- Coleta dados do survey
- Documenta resultados

---

## 🛠️ Desenvolvimento Local

### Compilar e executar testes
```bash
mvn clean test
```

### Executar apenas compilação
```bash
mvn compile
```

### Limpar arquivos compilados
```bash
mvn clean
```

### Gerar fat jar
```bash
mvn package
```

---

## 📊 Interpretação de Resultados

| F1 Score | Interpretação | Ação |
|----------|---------------|------|
| 100% | Excelente - Todas as chaves presentes | Manter padrão |
| 80-99% | Bom - Pouquíssimas omissões | Revisar pontos faltantes |
| 50-79% | Regular - Muitas omissões | Refatorar com chaves |
| 20-49% | Ruim - Maioria sem chaves | Refatoração urgente |
| 0-19% | Péssimo - Praticamente sem chaves | Completar refatoração |

---


## 📝 Licença

Este projeto está sob licença MIT.

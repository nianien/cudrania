# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`cudrania` (`io.github.nianien:cudrania`) is a single-module Java 11 utility library published to Maven Central. It is a *library*, not an application — there is no `main`. Code is a collection of loosely-coupled helpers under `com.cudrania`; most consumers pull in one or two utilities, so keep dependencies between subpackages minimal and mark heavyweight/optional integrations (`javax.mail`, `janino`, `logback`, `javassist`, `junit`) as `<optional>` in `pom.xml`.

Comments and Javadoc are written in **Chinese** — match that convention when editing existing files.

## Commands

```bash
mvn compile                              # build
mvn test                                 # run all tests (JUnit 5 / Jupiter, via surefire)
mvn test -Dtest=TestSqlStatement         # run a single test class
mvn test -Dtest=TestSqlStatement#method  # run a single test method
mvn install                              # install to local repo (see versioning note below)
mvn clean deploy -Possrh                 # release to Maven Central (OSSRH), requires GPG signing
```

Requires the compiler flag `--add-exports=java.base/jdk.internal.loader=ALL-UNNAMED` (already set in `pom.xml`) — some reflection utilities reach into JDK internals.

### Build-generated versioning (non-obvious)

The `install-jar` profile auto-activates whenever `src/main/java` exists and installs the artifact with a computed version:
`${project.version}.${git.commit.time}-${git.commit.id.abbrev}`. The `git.commit.*` properties come from `git-commit-id-plugin` (runs at the `initialize` phase). This means the *installed* artifact version differs from the `1.2.7` declared in the POM, and a commit is required for the git properties to resolve. Recent commit history centers on tuning this version format.

## Architecture / key packages

Three top-level areas under `src/main/java/com/cudrania/`: `core` (the bulk, ~92 files), `jdbc`, and `algorithm`.

- **`core/functions`** — the functional foundation the rest builds on.
  - `Fn` defines multi-arity functional interfaces (`Function`/`Consumer`/`BiConsumer` … up to 10 args: bi/tri/qua/qui/hex/hep/oct/non/dec) plus `Lambda`/`SneakyThrows`-style variants. Its interfaces extend `Serializable` so lambdas can be reflected back to their target `Method` via `SerializedLambda` (cached in a `WeakHashMap`). Several APIs (pipeline named params, bean property references) depend on this method-resolution trick.
  - `Fluent<T>` — wrap-any-object fluent chaining (`apply`/`accept`/`get`).
- **`core/pipeline`** — a builder-style pipeline DSL. `Pipelines.of(...)` starts a chain; `Pipeline`/`Pipeline2`/`Pipeline3` are the 1/2/3-input eval interfaces, all implemented by `Pipelines`. Steps are named (`Named`, `with(...)`) so later steps can pull earlier results by name out of `PipelineContext`. Step machinery lives in `core/pipeline/step`.
- **`core/reflection`** — `Reflections`, `Generics` (generic type resolution), `Methods`, `BeanProperty`/`Property`, `Classes`, `Primitive`. Central to the jdbc and json layers for mapping between rows/JSON and Java beans.
- **`core/json`** — `JsonParser` wraps a configurable Jackson `ObjectMapper`. `core/json/serializer` adds cross-cutting serialization behaviors driven by annotations/filters: field-level encryption (`SerEncryptor`/`SimpleSerEncryptor`, `SecurityPropertyFilter`/`SecurityPropertyWriter`), protobuf-style output (`Pb*`), and `@Description`-driven writers.
- **`jdbc`** — a lightweight JDBC toolkit, *not* a full ORM.
  - `jdbc/sql` — `SqlStatement`, `SqlGenerator`, `SqlOperator`, `DataField`, and `TypeConverter`/`SqlTypeConverter` build parameterized SQL.
  - `jdbc/query` — `SqlQuery` (interface) / `SqlQueryImpl` / `SqlQueryBuilder` execute statements; `ResultSetHandler` + `ResultSetAdapter` map result sets to objects.
  - `jdbc/table` — annotation-driven table/column mapping (`@Table`, `@Column`, `@Id`, `DataTable`/`DataTableFactory`, `TableHelper`).
  - `jdbc/datasource` — `DataSourceBuilder`.
- **`algorithm`** — standalone data-structure/algorithm implementations: `LoserTree`, `ConsistentHash`, `MassSorter` (external merge sort), `PriorityHeap`, `Calculator`.
- Other `core` utilities: `tree`, `collection`, `date`, `text`, `io`, `bits`, `math`, `xml`, `loader`, `proxy`, `exception`, `log/logback`.

## Tests

JUnit 5 tests live under `src/test/java/com/cudrania/test/<topic>/`, mirroring the main package topics. Database tests use in-memory **H2** + **HikariCP** (test-scope) with fixtures in `src/test/resources` (`datasource.xml`, `db.properties`, plus large `*.txt` datasets for the sort/algorithm tests). No network or external services are needed to run the suite.

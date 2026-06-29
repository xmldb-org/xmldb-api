# AGENTS.md — XML:DB API

## Project Overview
The XML:DB API is a Java specification defining interfaces for XML database drivers. It provides the API interfaces that driver developers implement and that applications develop against, along with a concrete `DatabaseManager` implementation. Published to Maven Central under `net.sf.xmldb-org:xmldb-api`.

## Build & Development

- **Language:** Java 17+ (tested against JDK 17 and 21)
- **Build system:** Gradle (use `./gradlew` wrapper)
- **Build:** `./gradlew build`
- **Run tests:** `./gradlew check`
- **Run benchmarks:** `./gradlew jmh`
- **Format code:** `./gradlew spotlessApply`
- **Check formatting:** `./gradlew spotlessCheck`

## Project Structure

```
src/main/java/org/xmldb/api/          # Main API
├── DatabaseManager.java              # Concrete manager implementation
├── base/                             # Core interfaces (Collection, Database, Resource, Service, etc.)
├── modules/                          # Module interfaces (XMLResource, BinaryResource, query services, etc.)
└── security/                         # Security model (ACL, permissions, principals)
src/test/java/org/xmldb/api/          # Unit tests (JUnit Jupiter)
src/jmh/java/org/xmldb/api/           # JMH benchmarks
```

## Code Style & Conventions

- Follow [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).
- Formatting is enforced via **Spotless** using the Eclipse formatter config at `gradle/xmldb-api-style.xml` and import order from `gradle/xmldb-api.importorder`.
- **Checkstyle** and **Error Prone** are applied during builds.
- All source files must include the XML:DB Initiative Software License header (see `gradle/LICENSE_HEADER`).

## Testing

- **Framework:** JUnit Jupiter (JUnit 5)
- **Assertions:** AssertJ (`org.assertj:assertj-core`)
- **Mocking:** Mockito with `mockito-junit-jupiter` extension
- **Logging:** `slf4j-mock` for test logging
- **Coverage:** JaCoCo (runs automatically after tests), reported to Codecov and SonarCloud

## CI/CD

- **CI:** GitHub Actions (`.github/workflows/gradle.yml`) — builds on push/PR to `main` and `protobuf` branches, runs nightly, matrix of JDK 17 and 21 on Ubuntu.
- **Static analysis:** Codacy, SonarCloud, Error Prone, Checkstyle.
- **Deploy:** Automatic publish to Sonatype (Maven Central) on non-PR pushes from the `xmldb-org/xmldb-api` repository.

## Dependencies

- **Runtime:** None (pure API specification)
- **Test:** AssertJ, Mockito, JMH, slf4j-mock
- **Dependency updates:** Managed via [Renovate](https://docs.renovatebot.com/) (`renovate.json`)

## Key Guidelines

- This is an **API specification library** — changes to interfaces are breaking changes for all implementations.
- The module name is `org.xmldb.api` (Java module system / JPMS).
- Keep the API minimal and backward-compatible.

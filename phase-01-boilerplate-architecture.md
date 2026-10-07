# Phase 1: Boilerplate and Architecture Setup

## 1. Phase title and purpose
**Phase 1: Boilerplate and Architecture Setup**
The purpose of this phase was to establish a clean, dependency-free Java web application boilerplate. The architecture was designed to be domain-independent so that a specific business idea and features (like authentication and registration) could be added later.

## 2. Starting state / assumptions
- Empty directory.
- Requirements mandated plain Java, plain HTML/CSS/JS, MySQL, and JDBC.
- Strict constraint against using any web or backend frameworks (e.g., no Spring Boot, no React/Angular).
- TDD and Guided Implementation practices were enforced.
- Assumed the existence of a local MySQL database for connection testing.

## 3. Final outcome
A vanilla Java web application was set up using Gradle. It features a built-in HTTP server capable of serving static frontend files and responding to basic API health checks. However, **there is a contradiction between the intended final state and the actual repository state regarding the database connection**, which is detailed below.

## 4. Implementation journey
1. **Project Initialization:** Created the basic Maven/Gradle standard directory structure and a `build.gradle` file configured for Java 17 and JUnit 5.
2. **First Test & Basic HTTP Server Setup:** Written a failing test for a `/api/health` endpoint. Implemented `MainServer` using `com.sun.net.httpserver.HttpServer` to make the test pass. The user fixed minor typos (`@test` to `@Test`, missing imports).
3. **Static File Server:** Wrote a test to verify delivery of `/index.html`. Updated `MainServer` with a catch-all context (`/`) to read files from `src/main/resources/public` as byte streams and return them.
4. **Database Connection Setup:** The intention was to add the MySQL driver to `build.gradle` and write a test to connect to a local MySQL instance using JDBC. The user reported a successful test run in the conversation, implying completion.

## 5. Final project structure
```text
project-root/
├── build.gradle
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/myapp/
│   │   │       ├── DatabaseManager.java
│   │   │       ├── MainServer.java
│   │   │       ├── User.java             (Discovered in repo, not guided)
│   │   │       └── UserRepository.java   (Discovered in repo, not guided)
│   │   └── resources/
│   │       └── public/
│   │           └── index.html
│   └── test/
│       └── java/
│           └── com/myapp/
│               ├── DatabaseManagerTest.java
│               ├── ServerTest.java
│               └── UserRepositoryTest.java (Discovered in repo, not guided)
```

## 6. Frontend architecture
- **Static Assets:** HTML, CSS, and JS files live in `src/main/resources/public`.
- **Delivery:** The Java `MainServer` reads the files using `MainServer.class.getResourceAsStream()` and serves the raw bytes directly to the browser.
- **Communication:** Future logic is intended to use the browser's native `fetch` API to communicate with REST endpoints on the Java backend.

## 7. Backend architecture
- **Server:** Uses the JDK's built-in `com.sun.net.httpserver.HttpServer`.
- **Routing:** Contexts are registered directly on the `HttpServer`. `/api/health` handles the health check, and `/` acts as a static file catch-all.
- **Dependencies:** Completely framework-free. Only uses standard library classes (`java.io.InputStream`, `java.io.OutputStream`, `java.net.InetSocketAddress`).

## 8. Database architecture
- **Setup:** A `DatabaseManager` class was created with standard JDBC boilerplate to connect to `jdbc:mysql://localhost:3306/vanilla_db` using `root`/`password`.
- **Contradiction:** The implementation history indicated the MySQL JDBC driver was added to `build.gradle` and the DB connection was tested. **However, inspecting the repository reveals the MySQL driver (`mysql-connector-j`) is missing from `build.gradle`.** 
- Additional domain classes (`User`, `UserRepository`) were found in the repository, though they were not part of the guided implementation phase.

## 9. Important implementation decisions
- **Server Choice:** Chose `com.sun.net.httpserver.HttpServer` instead of Servlet/Tomcat to strictly adhere to the "no framework / plain Java" constraint without requiring an external container.
- **JSON Parsing:** Intentionally deferred choosing a JSON parser (like Jackson/Gson) until complex data structures are actually needed.
- **Testing:** Opted for JUnit 5 as the sole test framework.

## 10. Dependencies and configuration
- **Java Version:** 17 (configured in `build.gradle`).
- **Test Framework:** JUnit Jupiter 5.10.0.
- **Port:** The server runs on `8080`.
- **Missing Dependency:** The `mysql-connector-j` dependency is notably absent from `build.gradle`.

## 11. Commands and operations
- Used standard PowerShell commands for directory creation (`mkdir -Force ...`).
- Used `gradle test` heavily to drive the TDD cycle.
- Manual execution of `MainServer.main` is intended to boot the application.

## 12. Verification
- **ServerTest:** Successfully verifies that `/api/health` returns `200 OK` and `/index.html` returns `200 OK` with the expected HTML content.
- **DatabaseManagerTest:** The repository shows this test is currently annotated with `@Disabled("Skipping until local MySQL is installed and running")`. It is not actively being verified.

## 13. Failures and fixes
- **Typo 1:** `AferEach` instead of `AfterEach` and `@test` instead of `@Test`. Fixed by providing corrected annotations.
- **Typo 2:** Missing static import for `assertEquals`. Fixed by explicitly including `import static org.junit.jupiter.api.Assertions.assertEquals;`.
- **Typo 3:** `asserTrue` instead of `assertTrue`. Fixed by correcting the method call.
- **Typo 4:** Missing `import java.io.InputStream;` in `MainServer`. Fixed by re-supplying the correct import list.

## 14. Security / operational considerations
- Hardcoded database credentials exist in `DatabaseManager.java`. This is acceptable for a local boilerplate but must be extracted to environment variables or a configuration file in the future.
- The static file server currently does not set correct MIME types (`Content-Type` headers) based on file extensions.

## 15. Out-of-scope / deferred work
The following have NOT been implemented yet:
- Registration
- Login
- Logout
- Sessions
- JWT
- Password reset
- Email verification
- OTP
- Authorization/roles
- Application-specific/domain functionality
- JSON Parsing library

## 16. Known limitations / unresolved questions
- **Database Driver:** The MySQL driver must be added back to `build.gradle` before `DatabaseManager.getConnection()` can succeed at runtime.
- **Live Database:** A local MySQL instance with `vanilla_db` must be running to enable and pass `DatabaseManagerTest`.

## 17. Commit linkage
- **Git Status:** The project is **not** currently a Git repository (`fatal: not a git repository`).
- **Commit:** N/A. There are uncommitted changes (the entire project is currently untracked by version control).

## 18. RESUME FROM HERE
**Exact State:**
The project compiles and the HTTP server test passes. However, the database connection test is explicitly `@Disabled` and the MySQL JDBC driver is missing from `build.gradle`. Furthermore, stub files for `User` and `UserRepository` have been created outside of the guided implementation.

**Next Steps for the next phase (Registration):**
1. Initialize a git repository to track changes.
2. Re-add the MySQL driver (`com.mysql:mysql-connector-j`) to `build.gradle`.
3. Set up a local MySQL instance, re-enable `DatabaseManagerTest`, and ensure the connection actually works.
4. Proceed with implementing the Registration feature (HTML Form -> JS -> Java Handler -> UserRepository -> MySQL) using TDD.
